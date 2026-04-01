package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileMessageService {
    public static void main(String[] args) throws FileNotFoundException {
        Message m = new Message("Inquiry","JaneDoe","Admin");
        System.out.println(m);
        try(FileOutputStream fos = new FileOutputStream("message.ser")
        ; ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(m);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
