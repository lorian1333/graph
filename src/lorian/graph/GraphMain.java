package lorian.graph;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.eclipse.jdt.internal.jarinjarloader.*;

public class GraphMain {
	private static String sSwtVersion = "4.3";

	public static void main(String[] args) throws Throwable {
		System.out.println("Graph v" + GraphFunctionsFrame.version);

		try {
			ClassLoader cl = getClassloader();
			Thread.currentThread().setContextClassLoader(cl);
		} catch (JarinJarLoadFailed ex) {
			String reason = ex.getMessage();
			System.err.println("Launch failed: " + reason);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JOptionPane.showMessageDialog(null, reason, "Launching UI Failed", 0);
			return;
		}

		GraphFunctionsFrame.funcframe = new GraphFunctionsFrame(false);
	}

	private static ClassLoader getClassloader() throws GraphMain.JarinJarLoadFailed {
		String swtFileName = getSwtJarName();
		String gluegenFileName = getGluegenJarName();
		String joglFileName = getJoglJarName();

		System.out.printf("%s\n%s\n%s\n", swtFileName, gluegenFileName, joglFileName); 
		try {
			//URL[] allNativeJarsUrl = new URL[] { new URL("rsrc:" + swtFileName), new URL("rsrc:" + gluegenFileName), new URL("rsrc:" + joglFileName) };

			URLClassLoader cl = (URLClassLoader) GraphMain.class.getClassLoader();
			URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
			Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			addUrlMethod.setAccessible(true);

			URL swtFileUrl = new URL("rsrc:" + swtFileName);
			URL gluegenFileUrl = new URL("rsrc:" + gluegenFileName);
			URL joglFileUrl = new URL("rsrc:" + joglFileName);
			URL gluegenRtFileUrl = new URL("rsrc:gluegen-rt.jar");
			URL joglAllFileUrl = new URL("rsrc:jogl-all.jar");
			
			
			addUrlMethod.invoke(cl, new Object[] {  swtFileUrl });
			addUrlMethod.invoke(cl, new Object[] {  gluegenRtFileUrl });
			addUrlMethod.invoke(cl, new Object[] {  joglAllFileUrl });
			addUrlMethod.invoke(cl, new Object[] {  gluegenFileUrl });
			addUrlMethod.invoke(cl, new Object[] {  joglFileUrl });
			
			return cl;
		} catch (Exception exx) {
			throw new JarinJarLoadFailed(exx.getClass().getSimpleName() + ": " + exx.getMessage());
		}
	}

	private static String getSwtJarName() throws GraphMain.JarinJarLoadFailed {
		String osName = System.getProperty("os.name").toLowerCase();
		String swtFileNameOsPart = (osName.contains("linux")) || (osName.contains("nix")) ? "linux" : osName.contains("mac") ? "osx" : osName.contains("win") ? "win" : "";

		if ("".equals(swtFileNameOsPart)) {
			throw new JarinJarLoadFailed("Unknown OS name: " + osName);
		}
		String swtFileNameArchPart = System.getProperty("os.arch").toLowerCase().contains("64") ? "64" : "32";
		String swtFileName = "swt-" + swtFileNameOsPart + swtFileNameArchPart + "-" + sSwtVersion + ".jar";
		return swtFileName;
	}

	private static String getGluegenJarName() throws GraphMain.JarinJarLoadFailed {
		String osName = System.getProperty("os.name").toLowerCase();
		String gluegenFileNameOsPart = (osName.contains("linux")) || (osName.contains("nix")) ? "linux" : osName.contains("mac") ? "macosx" : osName.contains("win") ? "windows" : "";

		if ("".equals(gluegenFileNameOsPart)) {
			throw new JarinJarLoadFailed("Unknown OS name: " + osName);
		}
		String gluegenFileNameArchPart = System.getProperty("os.arch").toLowerCase().contains("64") ? "amd64" : "i586";
		String gluegenFileName = "gluegen-rt-natives-" + gluegenFileNameOsPart + "-" + gluegenFileNameArchPart + ".jar";
		return gluegenFileName;
	}

	private static String getJoglJarName() throws GraphMain.JarinJarLoadFailed {
		String osName = System.getProperty("os.name").toLowerCase();
		String joglFileNameOsPart = (osName.contains("linux")) || (osName.contains("nix")) ? "linux" : osName.contains("mac") ? "macosx" : osName.contains("win") ? "windows" : "";

		if ("".equals(joglFileNameOsPart)) {
			throw new JarinJarLoadFailed("Unknown OS name: " + osName);
		}
		String joglFileNameArchPart = System.getProperty("os.arch").toLowerCase().contains("64") ? "amd64" : "i586";
		String joglFileName = "jogl-all-natives-" + joglFileNameOsPart + "-" + joglFileNameArchPart + ".jar";
		return joglFileName;
	}

	private static class JarinJarLoadFailed extends Exception {
		private static final long serialVersionUID = 1L;

		private JarinJarLoadFailed(String message) {
			super(message);
		}
	}
}
