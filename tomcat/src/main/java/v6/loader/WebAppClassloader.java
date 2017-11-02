package v6.loader;

import demo.servlets.HeaderServlet;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author wangqi
 * @date 2017/10/25 下午6:55
 *
 * Loader 的作用是提供 专用的ClassLoader, 用于加载 Servlet 和 Web应用 的应用类库，而不能加载其他的任何东西。
 * 这样可以提高安全性，Servlet中暴露的漏洞控制系统的 ClassLoader 进而对系统造成危害。
 *
 * 同时，开启后台线程定期刷新类文件，防止有更新发生。
 */
public class WebAppClassloader extends ClassLoader {
    private boolean autoReload;

    private Map<String, Class> loadedClass = new ConcurrentHashMap<>();
    private Set<String> notFoundClass = Collections.synchronizedSet(new HashSet<>());

    /**
     * 在系统Classloader 找不到的情况下，是否委托 父加载器
      */
    private boolean delegate = false;
    private ClassLoader parent = null;

    /**
     * The list of local repositories, in the order they should be searched
     * for locally loaded classes or resources.
     */
    private URL[] repositories;
    private ClassLoader repositoriesLoader;

    /**
     * 不允许加载的类
     */
    private Set<String> triggers = new HashSet<>();

    /**
     * 不允许加载的包
     */
    private Set<String> packageTriggers = new HashSet<>();

    public static void main(String[] args) {
        WebAppClassloader loader = new WebAppClassloader();
        try {
            Class clazz = loader.loadClass("demo.servlets.HeaderServlet");
            HeaderServlet instance = (HeaderServlet)clazz.newInstance();
            System.out.println(instance);
            loader.goLoadClass();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loader.goLoadClass();
        System.out.print( loader.loadedClass );
    }

    public WebAppClassloader(){
        this( false, false, null, null, null, null );
    }

    public WebAppClassloader(
            boolean autoReload,
            boolean delegate,
            ClassLoader parentLoader,
            String[] repositoriesPaths,
            Set<String> triggers,
            Set<String> packageTriggers
    ){
        this.autoReload = autoReload;
        this.delegate = delegate;
        this.parent = parentLoader;
        if(null == parent){
            this.delegate = false;
        }
        this.triggers = triggers==null ? new HashSet<>() : triggers;
        this.packageTriggers = packageTriggers==null ? new HashSet<>() : packageTriggers;

        if( triggers==null || triggers.size()==0 ){
            triggers = new HashSet<>();
            triggers.add("javax.servlet.Servlet");
        }

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL classesURL = classLoader.getResource("WEB-INF/classes");
        URL libURL = classLoader.getResource("WEB-INF/lib/");
//        URL rootUrl = classLoader.getResource("");

        List<URL> rep = new ArrayList<>();
        if( null!=repositoriesPaths && repositoriesPaths.length>0){
            for(int i = 0; i< repositoriesPaths.length; i++){
                String path = repositoriesPaths[i];
                // 防止设置相对路径，搞到根目录外面去
                if( !path.contains("..") ){
                    URL url = classLoader.getResource(path);
                    rep.add(url);
                }
            }
        }
        if( null != classesURL ){
            rep.add(classesURL);
        }
        if( null != libURL ){
            rep.add(libURL);
        }
//        if( null != rootUrl ){
//            rep.add(rootUrl);
//        }
        repositories = rep.toArray( new URL[rep.size()] );
        repositoriesLoader = new URLClassLoader(repositories);

//        goLoadCalss();
    }


    /**
     * 重写，比Tomcat的少了一些 SecurityManager 以及 日志 的东西。
     */
    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> clazz = null;

        // (0) Check our previously loaded local class cache
        clazz = findLoadedClass0(name);

        // (0.1) Check our system loaded class cache
        if (clazz == null) {
            clazz = findLoadedClass(name);
        }

        // (0.2) Try loading the class with the system class loader,
        //  to prevent the webapp from overriding standard J2SE classes
        if (clazz == null) {
            try {
                clazz = getSystemClassLoader().loadClass(name);
                resolveClass(clazz);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        // (0.3) Delegate to our parent if requested
        if (clazz==null && delegate && null!=parent) {
            try {
                clazz = Class.forName(name, false, parent);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        // (0.4) Search local repositories
        if (clazz == null) {
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        // (0.5) If still can't find in local findClass method, Delegate to parent unconditionally
        if (clazz==null && !delegate) {
            try {
                clazz = Class.forName(name, false, parent);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        if (clazz != null) {
            if (resolve) {
                /**
                 * 用于Link Class, 参考 https://docs.oracle.com/javase/specs/jls/se7/html/jls-12.html#jls-12.3
                 * Linking is the process of taking a binary form of a class or interface type and combining
                 * it into the run-time state of the Java Virtual Machine, so that it can be executed.
                 * A class or interface type is always loaded before it is linked.
                 */
                resolveClass(clazz);
            }
            return (clazz);
        }

        throw new ClassNotFoundException(name);
    }

    protected Class<?> findLoadedClass0(String name) {
        Class entry = loadedClass.get(name);
        if (entry != null) {
            return entry;
        }
        return null;
    }

    /**
     * 通常，自定义Classloader直接重写 findClass 方法就好了，因为在基础的 ClassLoader 中，默认的系统加载器都没有找到的时候会调用这个方法
     * 而基础的 ClassLoader 中是直接抛 ClassNotFoundException 异常的，即如果系统加载器没有加载，则当没有找到。
     * 而自定义类加载器则可以直接重写该方法，实现自己的加载逻辑。
     *
     * 在Tomcat中需要实现两个逻辑，查询禁止加载列表，查找配置的Jar包和路径。
     *
     * 比Tomcat中少了Jar包加载等内容。
     *
     * @param fullName 带有包名的完整类名
     */
    @Override
    public Class<?> findClass(String fullName) throws ClassNotFoundException {
        Class<?> clazz = null;

        if (loadedClass.containsKey(fullName)) {
            clazz = loadedClass.get(fullName);
        }

        if( null==clazz
            && !notFoundClass.contains(fullName)
            && !triggers.contains(fullName)
            && repositories.length > 0
        ) {
            int lastDot = fullName.lastIndexOf(".");
            if( lastDot > 0 ){
                String packName = fullName.substring(0, lastDot);
                if( !packageTriggers.contains(packName) ){
                    clazz = repositoriesLoader.loadClass( fullName );
                }
            }else {
                clazz = repositoriesLoader.loadClass( fullName );
            }

        }

        if( null == clazz){
            throw new ClassNotFoundException(fullName + " Not Found");
        }

        loadedClass.put(fullName, clazz);

        return clazz;
    }

    public void backgroundProcess() {
        goLoadClass();
    }

    private void goLoadClass(){
        for(int j=0; j<repositories.length; j++){
            try {
                String basePath = repositories[j].toURI().getRawPath();
                Map<String, Long> res = listLastModifiedTime(basePath);
                res.keySet().forEach( key -> {
                    if( key.endsWith(".class") ){
                        long lastMt = res.get(key);
                        long lastModifiedGap = System.currentTimeMillis() - lastMt;
                        // 如果是最近 15秒(这个值可以改成可配置的) 内更新的，或者没有被加载过
                        String classFullName = key.replace(".class", "");
                        if( lastModifiedGap <= 15*1000 || !loadedClass.containsKey(classFullName)){
                            try {
                                loadedClass.put(classFullName, repositoriesLoader.loadClass(classFullName));
                            } catch (ClassNotFoundException e) {
                                System.err.println( "Class Not Found:" + classFullName + " " + e.getLocalizedMessage() );
                            }
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println(repositories[j].toString() + " -> " + e.getLocalizedMessage());
            }
        }
    }

    private Map<String, Long> listLastModifiedTime(String basePath){
        return listLastModifiedTime(basePath, null);
    }
    private Map<String, Long> listLastModifiedTime(String basePath, String nameSeed){
        Map<String, Long> res = new HashMap<>();
        File file = new File(basePath);
        if( file.isFile() ){
            Long lastMT = file.lastModified();
            res.put(nameSeed, lastMT);
        }else {
            // 这是一个目录，则列举目录下所有内容
            String[] currentFileList = file.list();
            for( int i=0; i<currentFileList.length; i++ ){
                String fileName = currentFileList[i];
                String filePath = basePath + "/" + fileName;
//                File subFile = new File(filePath);
                Map<String, Long> current;
                if (null != nameSeed) {
                    current = listLastModifiedTime(filePath, nameSeed+"."+fileName);
                }else {
                    current = listLastModifiedTime(filePath, fileName);
                }
                res.putAll(current);
            }
        }
        return res;
    }
}
