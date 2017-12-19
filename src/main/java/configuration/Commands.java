package configuration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Commands {

    abstract static class BaseCommand {
        @Parameter(names = {"-h", "--host"}, description = "Host/IP to bind against")
        private String host = this.getDefaultHost();

        @Parameter(names = {"-p", "--port"}, description = "Port to bind against")
        private int port = this.getDefaultPort();

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        private String getDefaultHost() {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return "localhost";
            }
        }

        abstract int getDefaultPort();
    }

    @Parameters(commandDescription = "Start a master actor system")
    public static class MasterCommand extends BaseCommand {

        public static final int DEFAULT_PORT = 7877;

        @Parameter(names = {"-w", "--workers"}, description = "Number of workers to start locally")
        private int numLocalWorkers = 2;

        @Parameter(names = {"--path"}, description = "Path of the input csv file")
        private String path;

        @Override
        int getDefaultPort() {
            return DEFAULT_PORT;
        }

        public int getNumLocalWorkers() {
            return numLocalWorkers;
        }

        public String getPath() {
            return path;
        }
    }

    @Parameters(commandDescription = "Start a slave actor system")
    public static class SlaveCommand extends BaseCommand {

        public static final int DEFAULT_PORT = 7879;

        @Parameter(names = {"-m", "--master"}, description = "host[:port] of the master", required = true)
        private String master;

        public String getMasterHost() {
            int colonIndex = this.master.lastIndexOf(':');
            if (colonIndex == -1)
                return this.master;
            return this.master.substring(0, colonIndex);
        }

        public int getMasterPort() {
            int colonIndex = this.master.lastIndexOf(':');
            if (colonIndex == -1) {
                return MasterCommand.DEFAULT_PORT;
            }
            String portSpec = this.master.substring(colonIndex + 1);
            try {
                return Integer.parseInt(portSpec);
            } catch (NumberFormatException e) {
                throw new ParameterException(String.format("Illegal port: \"%s\"", portSpec));
            }
        }

        @Override
        int getDefaultPort() {
            return DEFAULT_PORT;
        }
    }
}
