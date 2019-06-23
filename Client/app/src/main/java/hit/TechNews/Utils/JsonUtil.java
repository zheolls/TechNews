package hit.TechNews.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
/**
 * Description : Json转换工具类
 */
public class JsonUtil {
    private static Gson mGson = new Gson();

//  将对象准换为json字符串

    public static <T> String serialize(T object) {
        return mGson.toJson(object);
    }

//    将json字符串转换为对象

    public static <T> T deserialize(String json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

//    将json字符串转换为List

    public static <T> List<T> deserializeList(String json, Class<T[]> clz) throws JsonSyntaxException {
        T[] array = mGson.fromJson(json, clz);
        return Arrays.asList(array);
    }

//    将json对象转换为实体对象

    public static <T> T deserialize(JsonObject json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

//    将json字符串转换为对象

    public static <T> T deserialize(String json, Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }
}
