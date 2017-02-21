package ln.vps.wxpub.command;

/**
 * Created by Helly on 2017/02/15.
 */
public class HelpCommander implements Commander {
    private static final String HELP_MSG = "目前支持命令有：\n" +
            "$help --输出本信息\n" +
            "$javadoc#<Class SimpleName> --输出Oracle JDK8 javadoc内相关类的URL地址，比如:\n\t$javadoc#String\n" +
            "$linuxman#<Manual Target> --输出Linux Manual内相关Manual的URL地址，比如:\n\t$linuxman#gcc\n" +
            "\n" +
            "#备注：所有命令都大小写敏感\n" +
            "\n" +
            "欢迎使用，会继续添加内容";

    public HelpCommander() {
    }

    @Override
    public String execute(String fromUserName, String cmd, String[] params) {
        return HELP_MSG;
    }
}
