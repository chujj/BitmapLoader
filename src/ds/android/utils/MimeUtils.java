package ds.android.utils;

import java.util.HashMap;
import java.util.Map;

public class MimeUtils {
	private static Map<String, String> extensionToMimeMap = new HashMap<String, String>();
	private static Map<String, String> mimeToExtensionMap = new HashMap<String, String>();
	
	private static void insert(String key, String value) {
		extensionToMimeMap.put(key, value);
		mimeToExtensionMap.put(value, key);
	}
	
	private static String 	DEFAULT_MIME = "application/octet-stream";
	private static String   DEFAULT_EXTENSION = "";
	
	static {
		insert("saveme","application/octet-stream");
        insert("dump","application/octet-stream");
        insert("hqx","application/octet-stream");
        insert("arc","application/octet-stream");
        insert("obj","application/octet-stream");
        insert("lib","application/octet-stream");
        insert("bin","application/octet-stream");
        insert("exe","application/octet-stream");
        insert("zip","application/octet-stream");
        insert("gz","application/octet-stream");
        insert("oda","application/oda");
        insert("pdf","application/pdf");
        insert("eps","application/postscript");
        insert("ai","application/postscript");
        insert("ps","application/postscript");
        insert("rtf","application/rtf");
        insert("dvi","application/x-dvi");
        insert("hdf","application/x-hdf");
        insert("latex","application/x-latex");
        insert("nc","application/x-netcdf");
        insert("cdf","application/x-netcdf");
        insert("tex","application/x-tex");
        insert("texinfo","application/x-texinfo");
        insert("texi","application/x-texinfo");
        insert("t","application/x-troff");
        insert("tr","application/x-texinfo");
        insert("roff","application/x-texinfo");
        insert("man","application/x-troff-man");
        insert("me","application/x-troff-me");
        insert("ms","application/x-troff-ms");
        insert("src","application/x-wais-source");
        insert("wsrc","application/x-wais-source");
        insert("zip","application/zip");
        insert("bcpio","application/x-bcpio");
        insert("cpio","application/x-cpio");
        insert("gtar","application/x-gtar");
        insert("shar","application/x-shar");
        insert("sh","application/x-shar");
        insert("sv4cpio","application/x-sv4cpio");
        insert("sv4crc","application/x-sv4crc");
        insert("tar","application/x-tar");
        insert("ustar","application/x-ustar");
        insert("amr","audio/amr");
        insert("snd","audio/basic");
        insert("au","audio/basic");
        insert("aifc","audio/x-aiff");
        insert("aif","audio/x-aiff");
        insert("aiff","audio/x-aiff");
        insert("wav","audio/x-wav");
        insert("gif","image/gif");
        insert("ief","image/ief");
        insert("jfif","image/gif");
        insert("jfif-tbnl","image/gif");
        insert("jpe","image/gif");
        insert("jpg","image/gif");
        insert("jpeg","image/jpeg");
        insert("tif","image/tiff");
        insert("tiff","image/tiff");
        insert("fpx","image/vnd.fpx");
        insert("fpix","image/vnd.fpx");
        insert("ras","image/x-cmu-rast");
        insert("pnm","image/x-portable-anymap");
        insert("bpm","image/x-portable-bitmap");
        insert("pgm","image/x-portable-graymap");
        insert("ppm","image/x-portable-pixmap");
        insert("rgb","image/x-rgb");
        insert("xbm","image/x-xbitmap");
        insert("xpm","image/x-xbitmap");
        insert("xwd","image/x-xwindowdump");
        insert("png","image/png");
        insert("htm","text/html");
        insert("html","text/html");
        insert("text","text/plain");
        insert("c","text/plain");
        insert("cc","text/plain");
        insert("c++","text/plain");
        insert("h","text/plain");
        insert("pl","text/plain");
        insert("txt","text/plain");
        insert("java","text/plain");
        insert("el","text/plain");
        insert("tsv","text/tab-separated-values");
        insert("etx","text/x-setext");
        insert("mpg","video/mpeg");
        insert("mpe","video/mpeg");
        insert("mpeg","video/mpeg");
        insert("mov","video/quicktime");
        insert("qt","video/quicktime");
        insert("avi","application/x-troff-msvideo");
        insert("movie","video/x-sgi-movie");
        insert("mv","video/x-sgi-movie");
        insert("mime","message/rfc822");
        insert("xml","application/xml");
	}
	
	public static String getMimeTypeFromExtension(String extension) {
		String mime = extensionToMimeMap.get(extension);
		if(mime == null) {
			mime = DEFAULT_MIME;
		}
		return mime;
	}
	public static String getExtensionFromMimeType(String mimeType) {
		String extension = mimeToExtensionMap.get(mimeType);
		if(extension == null) {
			extension = DEFAULT_EXTENSION;
		}
		return extension;
	}
}
