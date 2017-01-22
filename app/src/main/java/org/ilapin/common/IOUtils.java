package org.ilapin.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class IOUtils {

	public static String readInputStreamToString(final InputStream is) throws IOException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) > 0) {
			os.write(buffer, 0, bytesRead);
		}
		return os.toString("UTF-8");
	}

	public static byte[] readInputStreamToArray(final InputStream inputStream, final long skip) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		if (inputStream.skip(skip) != skip) {
			throw new RuntimeException(String.format(Locale.US, "Failed to skip %d byte(s)", skip));
		}

		int bytesRead;
		final byte[] buffer = new byte[1024];
		while ((bytesRead = inputStream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, bytesRead);
		}

		return byteArrayOutputStream.toByteArray();
	}
}
