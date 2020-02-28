package auth.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @description: bean工具类
 * @date 2019/02/21 09:22
 * @author Zhifeng.Zeng
 */
@Slf4j
public class BeanUtils<DO, VO> {

    /**
     * 属性拷贝方法，忽略入参值为空
     * @author Zhifeng.Zeng
     * @param src
     * @param target
     */
    public static void copyPropertiesIgnoreNull(Object src, Object target){
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


}
