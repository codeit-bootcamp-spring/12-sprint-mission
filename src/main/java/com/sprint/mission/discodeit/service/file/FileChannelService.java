package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.*;

public class FileChannelService {
    public static void main(String[] args) throws FileNotFoundException {
        Channel c = new Channel("General", "General","General","Channel for general inquiries","All users");
        System.out.println(c);
        try(FileOutputStream fos = new FileOutputStream("channel.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
