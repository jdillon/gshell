/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonatype.gshell.commands.ssh;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.common.util.NoCloseInputStream;
import org.apache.sshd.common.util.NoCloseOutputStream;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import java.io.IOException;

/**
 * Connect to a SSH server.
 *
 * @since 2.3
 */
@Command(name="ssh")
public class SshCommand
    extends CommandActionSupport
{
    @Option(name="-l", aliases={"--username"})
    private String username;

    @Option(name="-P", aliases={"--password"})
    private String password;

    @Option(name="-p", aliases={"--port"})
    private int port = 22;

    @Argument(index = 0)
    private String hostname;

	private ClientSession session;

    private String sshClientId;

    public void setSshClientId(String sshClientId) {
        this.sshClientId = sshClientId;
    }

    public Object execute(CommandContext context) throws Exception {
        //
        // TODO: Parse hostname for <username>@<hostname>
        //

        System.out.println("Connecting to host " + hostname + " on port " + port);

        // If the username/password was not configured via cli, then prompt the user for the values
        if (username == null || password == null) {
            log.debug("Prompting user for credentials");
            if (username == null) {
                username = readLine("Login: ");
            }
            if (password == null) {
                password = readLine("Password: ");
            }
        }

        // Create the client from prototype
        SshClient client = (SshClient) container.getComponentInstance(sshClientId);
        log.debug("Created client: {}", client);
        client.start();

        try {
            ConnectFuture future = client.connect(hostname, port);
            future.await();
            session = future.getSession();
            try {
                System.out.println("Connected");

                session.authPassword(username, password);
                int ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
                if ((ret & ClientSession.AUTHED) == 0) {
                    System.err.println("Authentication failed");
                    return null;
                }

                ClientChannel channel = session.createChannel("shell");
                channel.setIn(new NoCloseInputStream(System.in));
                ((ChannelShell) channel).setupSensibleDefaultPty();
                channel.setOut(new NoCloseOutputStream(System.out));
                channel.setErr(new NoCloseOutputStream(System.err));
                channel.open();
                channel.waitFor(ClientChannel.CLOSED, 0);
            }
            finally {
                session.close(false);
            }
        }
        finally {
            client.stop();
        }

        return null;
    }

    public String readLine(String msg) throws IOException {
        StringBuffer sb = new StringBuffer();
        System.err.print(msg);
        System.err.flush();
        for (;;) {
            int c = super.session.getKeyboard().read();
            if (c < 0) {
                return null;
            }
            System.err.print((char) c);
            if (c == '\r' || c == '\n') {
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

}
