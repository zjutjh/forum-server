package org.jh.forum.common.serialization;

import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2FactoryManager;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectInput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2Serialization;
import org.apache.dubbo.rpc.model.FrameworkModel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

/**
 * 自定义 Hessian2 序列化实现，允许非 Serializable 对象
 *
 * @author SugarMGP
 */
public class CustomHessian2Serialization extends Hessian2Serialization {
    @Override
    public ObjectOutput serialize(URL url, OutputStream out) {
        CustomHessian2FactoryManager hessian2FactoryManager = Optional.ofNullable(url).map(URL::getOrDefaultFrameworkModel).orElseGet(FrameworkModel::defaultModel).getBeanFactory().getOrRegisterBean(CustomHessian2FactoryManager.class);
        return new Hessian2ObjectOutput(out, hessian2FactoryManager);
    }

    @Override
    public ObjectInput deserialize(URL url, InputStream input) {
        CustomHessian2FactoryManager hessian2FactoryManager = Optional.ofNullable(url).map(URL::getOrDefaultFrameworkModel).orElseGet(FrameworkModel::defaultModel).getBeanFactory().getOrRegisterBean(CustomHessian2FactoryManager.class);
        return new Hessian2ObjectInput(input, hessian2FactoryManager);
    }

    public static class CustomHessian2FactoryManager extends Hessian2FactoryManager {
        public CustomHessian2FactoryManager(FrameworkModel frameworkModel) {
            super(frameworkModel);
        }

        @Override
        public SerializerFactory getSerializerFactory(ClassLoader classLoader) {
            SerializerFactory factory = super.getSerializerFactory(classLoader);
            if (!factory.isAllowNonSerializable()) {
                factory.setAllowNonSerializable(true);
            }
            return factory;
        }
    }
}