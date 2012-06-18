package vitro.tools;

import java.io.*;

public class FileClassLoader extends ClassLoader {

	protected Class findClass(String fileName) throws ClassNotFoundException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(fileName));
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			int size = 0;
			while(true) {
				int res = in.read();
				if (res == -1) { break; }
				data.write(res);
				size++;
			}
			return defineClass(null, data.toByteArray(), 0, size);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new ClassNotFoundException(fileName);
		}
	}
}