package com.ethan.agent.asm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.beans.factory.annotation.Value;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 增强 {@link  Value#value()} ()}, 如果未配置默认值，添加默认值空
 * @author EthanCao
 * @description spring-creed-agent
 * @date 15/11/24
 */
@Slf4j
public class PropertyValueTransformerEnhance implements ClassFileTransformer, Opcodes {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 只保留我们需要增强的类
        if (StringUtils.startsWith(className, "com/ethan")
                && StringUtils.endsWithAny(className, "Config", "Configuration")) {
            log.trace("@.@[PropertyValueTransformerEnhance className:{}]@.@", className);
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, 0);
            var cv = new ModifyClassVisitor(ASM9, cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return null;
    }
    static class ModifyAnnotationVisitor extends AnnotationVisitor {
        private final String fieldDescriptor;
        public ModifyAnnotationVisitor(int api, AnnotationVisitor annotationVisitor, String fieldDescriptor) {
            super(api, annotationVisitor);
            this.fieldDescriptor = fieldDescriptor;
        }

        @Override
        public void visit(String name, Object value) {
            log.trace("@.@[ModifyAnnotationVisitor name:" + name + " value:" + value + " fieldDescriptor:]@.@" + fieldDescriptor);
            if (value instanceof String) {
                String val = (String) value;
                // 若存在"${spring.application.name}",将其替换为${spring.application.name:}
                if (val.matches("\\$\\{[^\\:\\}]+\\}")) {
                    String modifiedValue;
                    if ("Z".equals(fieldDescriptor)) {
                        modifiedValue = val.replace("}", ":false}");
                    } else if ("Ljava/lang/Integer;".equals(fieldDescriptor) || "I".equals(fieldDescriptor)) {
                        modifiedValue = val.replace("}", ":10}");
                    } else {
                        modifiedValue = val.replace("}", ":}");
                    }
                    log.debug("@.@[ModifyAnnotationVisitor value from:{} to:{}]@.@", value, modifiedValue);
                    super.visit(name, modifiedValue);
                    return;
                }
            }
            super.visit(name, value);
        }


        @Override
        public void visitEnd() {
            log.trace("@.@[ModifyAnnotationVisitor visitEnd]@.@");
            super.visitEnd();
        }
    }

    static class ModifyFieldVisitor extends FieldVisitor {

        private final String fieldDescriptor;
        public ModifyFieldVisitor(int api, FieldVisitor fieldVisitor, String fieldDescriptor) {
            super(api, fieldVisitor);
            this.fieldDescriptor = fieldDescriptor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

            if ("Lorg/springframework/beans/factory/annotation/Value;".equals(descriptor)) {
                log.trace("@.@[PropertyValueTransformerEnhance visitAnnotation descriptor:{}]@.@", descriptor);
                return new ModifyAnnotationVisitor(Opcodes.ASM9, super.visitAnnotation(descriptor, visible), fieldDescriptor);
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }

    static class ModifyClassVisitor extends ClassVisitor {
        protected ModifyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        /**
         * 修改字段 的值
         * @param access the field's access flags (see {@link Opcodes}). This parameter also indicates if
         *     the field is synthetic and/or deprecated.
         * @param name the field's name.
         * @param descriptor the field's descriptor (see {@link Type}).
         * @param signature the field's signature. May be {@literal null} if the field's type does not use
         *     generic types.
         * @param value the field's initial value. This parameter, which may be {@literal null} if the
         *     field does not have an initial value, must be an {@link Integer}, a {@link Float}, a {@link
         *     Long}, a {@link Double} or a {@link String} (for {@code int}, {@code float}, {@code long}
         *     or {@code String} fields respectively). <i>This parameter is only used for static
         *     fields</i>. Its value is ignored for non static fields, which must be initialized through
         *     bytecode instructions in constructors or methods.
         * @return
         */
        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            log.trace("@.@[PropertyValueTransformerEnhance visitField name:{}]@.@", name);
            FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
            return new ModifyFieldVisitor(Opcodes.ASM9, fv, descriptor);
        }
    }

}
