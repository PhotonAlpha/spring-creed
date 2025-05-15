package com.ethan.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 4/12/24
 */
public class AgentMainApplication {
    public static void main(String[] args) throws Exception {
        while (true){
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (int i = 0; i < list.size(); i++) {
                VirtualMachineDescriptor jvm = list.get(i);;
                System.out.println("[" + i + "]ID:" + jvm.id() + ",Name:" + jvm.displayName());
            }
            System.out.println("请选择第几个");
            String regex = "^\\d+$";
            Pattern regexPattern = Pattern.compile("(?<id>\\d+)");

            // Scanner scanner = new Scanner(System.in);
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            String str = buf.readLine();

            boolean isNumber = str.matches(regex);
            int id;
            boolean exit = false;
            if (isNumber) {
                System.out.println("isNumber:" + isNumber);
                id = Integer.parseInt(str);
            } else {
                Matcher matcher = regexPattern.matcher(str);
                String idStr = "0";
                if (matcher.find()) {
                    idStr = matcher.group("id");
                }
                id = Integer.parseInt(idStr);
                exit = true;
            }
            System.out.println("id is:" + id);

            VirtualMachineDescriptor virtualMachineDescriptor = list.get(id);
            // VirtualMachine attach = VirtualMachine.attach(virtualMachineDescriptor.id());
            String agentJar = "./spring-creed-agent/target/creed-dev-buddy.jar";
            File file = new File(agentJar);
            System.out.println(file.exists());
            // 依附到目标jvm
            // attach.loadAgent(agentJar,"param");

            // detach 离开
            // attach.detach();
            var sha256Hex = DigestUtils.sha256Hex(Files.readAllBytes(file.toPath()));
            System.out.println("文件的Hash值：" + sha256Hex);
            if (exit) {
                ByteBuddyAgent.attach(file, virtualMachineDescriptor.id(), "exit");
            } else {
                ByteBuddyAgent.attach(file, virtualMachineDescriptor.id());
            }
        }
    }
}
