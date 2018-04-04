package hk.polyu.comp.diff_java;

import hk.polyu.comp.diff_java.changedistiller.CdUtil;
import py4j.GatewayServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by Ls CHEN.
 */
public class EntryPoint {
    private static final Path LOG_PATH = Paths.get(new File("parser.log").getAbsolutePath());
    static GatewayServer gatewayServer;

    public static void main(String[] args) {
        SimpleLogger.start(LOG_PATH, SimpleLogger.LogLevel.DEBUG);
        if (args.length > 0) {
            String action = args[0];
            if (Objects.equals(action, "startServer"))
                startGWServer();
            else if (action.equals("isDesiredModifications"))
                isDesiredModifications(args[1], args[2]);
            else
                System.out.println("Cannot recognize arg: " + action);
        }
    }

    private static void startGWServer() {
        gatewayServer = new GatewayServer(new EntryPoint());
        try {
            gatewayServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("GatewayServer start.");
    }

    public static boolean isDesiredModifications(String parent_path, String child_path) {
        SimpleLogger.info("Processing: " + parent_path + " : " + child_path);
        File oldF = new File(parent_path);
        File newF = new File(child_path);
        if (CdUtil.isDesiredChange(oldF, newF) != null) {
            SimpleLogger.info("Desired!");
            return true;
        } else
            SimpleLogger.info("Undesired.");
        return false;

    }

    public static void exit() {
        gatewayServer.shutdown();
        System.out.println("GatewayServer exit.");

    }
}
