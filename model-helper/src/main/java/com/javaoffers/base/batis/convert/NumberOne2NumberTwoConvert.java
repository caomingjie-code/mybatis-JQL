package com.javaoffers.base.batis.convert;

import com.javaoffers.base.batis.consistant.ModelConsistants;
import com.javaoffers.base.batis.core.ConverDescriptor;
import com.javaoffers.base.batis.core.Register;

import java.lang.reflect.Method;

import static com.javaoffers.base.batis.consistant.ModelConsistants.numberPrimitivesMapping;

/**
 *
 * NumberOne2NumberTwo
 */
public class NumberOne2NumberTwoConvert extends AbstractConver<Number,Number> {

    ConverDescriptor descriptor;

    Method convertMethod;

    @Override
    public Number convert(Number number) {
        double src = Double.parseDouble(number.toString());
        return (Number) descriptor.getDes().cast(src);
    }

    @Override
    public void register(Register register) {
        Class[] basePrimitiveClass = ModelConsistants.basePrimitiveClass;
        for (Class src : basePrimitiveClass) {
            for (Class des : basePrimitiveClass) {
                try {
                    ConverDescriptor descriptor = new ConverDescriptor(numberPrimitivesMapping.get(src), numberPrimitivesMapping.get(des));
                    final NumberOne2NumberTwoConvert n2n = new NumberOne2NumberTwoConvert();
                    n2n.setDescriptor(descriptor);
                    Method method = Number.class.getDeclaredMethod(des.getSimpleName() + "Value");
                    n2n.setConvertMethod(method);
                    method.setAccessible(true);
                    register.registerConvert(descriptor, n2n);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void setDescriptor(ConverDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void setConvertMethod(Method convertMethod) {
        this.convertMethod = convertMethod;
    }

}
