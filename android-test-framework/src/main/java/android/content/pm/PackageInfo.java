package android.content.pm;

/**
 * Overall information about the contents of a package.  This corresponds
 * to all of the information collected from AndroidManifest.xml.
 *
 * @author andoni
 * @since 1.0.0
 */
public class PackageInfo {

    public String packageName;
    public int versionCode;
    public String versionName;
    public String sharedUserId;
    public int sharedUserLabel;

}
