package v4.container;

import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import v4.container.valve.SimpleBasicValve;

import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author wangqi
 */
public class SimpleWrapper implements Wrapper {

  private Servlet instance = null;
  private String servletClass;
  private Loader loader;
  private SimplePipeline pipeline = new SimplePipeline(this);
  protected Container parent = null;

  public SimpleWrapper() {
    pipeline.setBasic(new SimpleBasicValve(this));
  }

  @Override
  public String getServletClass() {
    return this.servletClass;
  }

  @Override
  public void setServletClass(String servletClass) {
    this.servletClass = servletClass;
  }

  @Override
  public Loader getLoader() {
    return this.loader;
  }

  @Override
  public void setLoader(Loader loader) {
    this.loader = loader;
  }

  @Override
  public Servlet allocate() throws ServletException {
    // Load and initialize our instance if necessary
    if (instance==null) {
      try {
        instance = loadServlet();
      } catch (ServletException e) {
        throw e;
      } catch (Throwable e) {
        throw new ServletException("Cannot allocate a servlet instance", e);
      }
    }
    return instance;
  }

  private Servlet loadServlet() throws ServletException {
    if (instance!=null) {
      return instance;
    }

    Servlet servlet = null;
    String actualClass = servletClass;
    if (actualClass == null) {
      throw new ServletException("servlet class has not been specified");
    }

    Loader loader = getLoader();
    // Acquire an instance of the class loader to be used
    if (loader==null) {
      throw new ServletException("No loader.");
    }
    ClassLoader classLoader = loader.getClassLoader();

    // Load the specified servlet class from the appropriate class loader
    Class classClass = null;
    try {
      if (classLoader!=null) {
        classClass = classLoader.loadClass(actualClass);
      }
    }
    catch (ClassNotFoundException e) {
      throw new ServletException("Servlet class not found");
    }
    // Instantiate and initialize an instance of the servlet class itself
    try {
      servlet = (Servlet) classClass.newInstance();
    }
    catch (Throwable e) {
      throw new ServletException("Failed to instantiate servlet");
    }

    // Call the initialization method of this servlet
    try {
      servlet.init(null);
    }
    catch (Throwable f) {
      throw new ServletException("Failed initialize servlet.");
    }
    return servlet;
  }


  @Override
  public long getAvailable() {
    return 0;
  }

  @Override
  public void setAvailable(long available) {

  }

  @Override
  public String getJspFile() {
    return null;
  }

  @Override
  public void setJspFile(String jspFile) {

  }

  @Override
  public int getLoadOnStartup() {
    return 0;
  }

  @Override
  public void setLoadOnStartup(int value) {

  }

  @Override
  public String getRunAs() {
    return null;
  }

  @Override
  public void setRunAs(String runAs) {

  }

  @Override
  public String[] getServletMethods() throws ServletException {
    return new String[0];
  }

  @Override
  public boolean isUnavailable() {
    return false;
  }

  @Override
  public Servlet getServlet() {
    return null;
  }

  @Override
  public void setServlet(Servlet servlet) {

  }

  @Override
  public void addInitParameter(String name, String value) {

  }

  @Override
  public void addInstanceListener(InstanceListener listener) {

  }

  @Override
  public void addMapping(String mapping) {

  }

  @Override
  public void addSecurityReference(String name, String link) {

  }

  @Override
  public void deallocate(Servlet servlet) throws ServletException {

  }

  @Override
  public String findInitParameter(String name) {
    return null;
  }

  @Override
  public String[] findInitParameters() {
    return new String[0];
  }

  @Override
  public String[] findMappings() {
    return new String[0];
  }

  @Override
  public String findSecurityReference(String name) {
    return null;
  }

  @Override
  public String[] findSecurityReferences() {
    return new String[0];
  }

  @Override
  public void incrementErrorCount() {

  }

  @Override
  public void load() throws ServletException {

  }

  @Override
  public void removeInitParameter(String name) {

  }

  @Override
  public void removeInstanceListener(InstanceListener listener) {

  }

  @Override
  public void removeMapping(String mapping) {

  }

  @Override
  public void removeSecurityReference(String name) {

  }

  @Override
  public void unavailable(UnavailableException unavailable) {

  }

  @Override
  public void unload() throws ServletException {

  }

  @Override
  public MultipartConfigElement getMultipartConfigElement() {
    return null;
  }

  @Override
  public void setMultipartConfigElement(MultipartConfigElement multipartConfig) {

  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public void setAsyncSupported(boolean asyncSupport) {

  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public void setEnabled(boolean enabled) {

  }

  @Override
  public String getInfo() {
    return null;
  }

  @Override
  public Log getLogger() {
    return null;
  }

  @Override
  public Manager getManager() {
    return null;
  }

  @Override
  public void setManager(Manager manager) {

  }

  @Override
  public Object getMappingObject() {
    return null;
  }

  @Override
  public ObjectName getObjectName() {
    return null;
  }

  @Override
  public Pipeline getPipeline() {
    return null;
  }

  @Override
  public Cluster getCluster() {
    return null;
  }

  @Override
  public void setCluster(Cluster cluster) {

  }

  @Override
  public int getBackgroundProcessorDelay() {
    return 0;
  }

  @Override
  public void setBackgroundProcessorDelay(int delay) {

  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public void setName(String name) {

  }

  @Override
  public Container getParent() {
    return null;
  }

  @Override
  public void setParent(Container container) {

  }

  @Override
  public ClassLoader getParentClassLoader() {
    return null;
  }

  @Override
  public void setParentClassLoader(ClassLoader parent) {

  }

  @Override
  public Realm getRealm() {
    return null;
  }

  @Override
  public void setRealm(Realm realm) {

  }

  @Override
  public DirContext getResources() {
    return null;
  }

  @Override
  public void setResources(DirContext resources) {

  }

  @Override
  public void backgroundProcess() {

  }

  @Override
  public void addChild(Container child) {

  }

  @Override
  public void addContainerListener(ContainerListener listener) {

  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {

  }

  @Override
  public Container findChild(String name) {
    return null;
  }

  @Override
  public Container[] findChildren() {
    return new Container[0];
  }

  @Override
  public ContainerListener[] findContainerListeners() {
    return new ContainerListener[0];
  }

  @Override
  public void invoke(Request request, Response response) throws IOException, ServletException {

  }

  @Override
  public void removeChild(Container child) {

  }

  @Override
  public void removeContainerListener(ContainerListener listener) {

  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {

  }

  @Override
  public void fireContainerEvent(String type, Object data) {

  }

  @Override
  public void logAccess(Request request, Response response, long time, boolean useDefault) {

  }

  @Override
  public AccessLog getAccessLog() {
    return null;
  }

  @Override
  public void addLifecycleListener(LifecycleListener listener) {

  }

  @Override
  public LifecycleListener[] findLifecycleListeners() {
    return new LifecycleListener[0];
  }

  @Override
  public void removeLifecycleListener(LifecycleListener listener) {

  }

  @Override
  public void init() throws LifecycleException {

  }

  @Override
  public void start() throws LifecycleException {

  }

  @Override
  public void stop() throws LifecycleException {

  }

  @Override
  public void destroy() throws LifecycleException {

  }

  @Override
  public LifecycleState getState() {
    return null;
  }
}