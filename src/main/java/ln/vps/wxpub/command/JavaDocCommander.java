package ln.vps.wxpub.command;

import ln.vps.wxpub.util.LruMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Helly on 2017/02/15.
 */
public class JavaDocCommander implements Commander {
    private static final String JAVADOC_8_URL_PREFIX = "http://docs.oracle.com/javase/8/docs/api/";
    private static final String JAVADOC_8_URL_POSTFIX = ".html";
    private static final int MAX_SIZE = 2000;
    private LruMap<String, String> classesMap = new LruMap<>(MAX_SIZE, LruMap.KeepOrder.ACCESS_ORDER);
    private Set<String> packages;

    public JavaDocCommander() {
        this.packages = getPackages();
    }

    @Override
    public String execute(String fromUserName, String cmd, String[] params) {
        String classSimpleName = params[0].trim();
        List<String> classUrlParts = findBySimpleName(classSimpleName);
        if (classUrlParts == null) {
            return "通过简要名，无法找到此类:" + classSimpleName + "\n目前只支持JDK下相关类。";
        } else {
            StringBuilder buf = new StringBuilder();
            classUrlParts.forEach(urlPart -> buf.append(JAVADOC_8_URL_PREFIX)
                    .append(urlPart)
                    .append(JAVADOC_8_URL_POSTFIX)
                    .append('\n')
                    .append('\n'));
            buf.setLength(buf.length() - 2);
            return buf.toString();
        }
    }

    private List<String> findBySimpleName(String classSimpleName) {
        List<String> fqns = new ArrayList<>();
        for (String aPackage : packages) {
            String fqn = aPackage + "." + classSimpleName;
            if (classesMap.containsKey(fqn) || checkClass(fqn)) {
                fqns.add(classesMap.get(fqn));
            }
        }
        return fqns;
    }

    private boolean checkClass(String fqn) {
        try {
            Class.forName(fqn);
            classesMap.put(fqn, fqn.replaceAll("\\.", "\\/"));
            return true;
        } catch (Exception e) {
            // Ignore
            return false;
        }
    }

    private Set<String> getPackages() {
        Set<String> packages = new HashSet<>();
        for (Package aPackage : Package.getPackages()) {
            packages.add(aPackage.getName());
        }
        return packages;
    }
}
