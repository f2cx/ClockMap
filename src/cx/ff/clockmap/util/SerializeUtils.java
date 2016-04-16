package cx.ff.clockmap.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils {

    public static Object deserialize(String fileName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        Object obj;
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            obj = ois.readObject();
        }
        return obj;
    }

    public static void serialize(Object obj, String fileName)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        }
    }

}
