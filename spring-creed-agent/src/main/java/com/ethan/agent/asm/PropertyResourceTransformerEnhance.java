package com.ethan.agent.asm;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.springframework.context.annotation.PropertySource;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 增强/修改 {@link  PropertySource#ignoreResourceNotFound()}, 使其默认值为 true
 *
 * 修改目标：
 *      1. org.springframework.context.annotation.ConfigurationClassParser
 *      2.org.springframework.context.annotation.PropertySourceRegistry#processPropertySource -> ignoreResourceNotFound
 *
 * @author EthanCao
 * @description spring-creed-agent
 * @date 15/11/24
 */
@Slf4j
public class PropertyResourceTransformerEnhance implements ClassFileTransformer, Opcodes {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 只保留我们需要增强的类
        if ("org.springframework.context.annotation.PropertySource".equals(className.replace("/", "."))) {
            log.info("PropertyResourceTransformerEnhance:{} replaced:{}", className, className.replace("@.@[/", ".]@.@"));
            ClassReader cr = null;
            try {
                cr = new ClassReader(classfileBuffer);
            } catch (Exception e) {
                log.error("@.@[PropertyResourceTransformerEnhance ClassReader Exception]@.@", e);
            }
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            var cv = new PropertySourceClassVisitor(ASM9, cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return null;
    }

    static class PropertySourceAnnotationVisitor extends AnnotationVisitor {
        public PropertySourceAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            // log.debug("@.@[ModifyAnnotationVisitor name:]@.@" + name);
            log.debug("@.@[ModifyAnnotationVisitor value to true]@.@");
            // 将读取的默认值修改为true
            super.visit(name, true);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            log.debug("@.@[ModifyAnnotationVisitor visitAnnotation:]@.@" + name);
            log.debug("@.@[ModifyAnnotationVisitor visitAnnotation:]@.@" + descriptor);
            return super.visitAnnotation(name, descriptor);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            log.debug("@.@[ModifyAnnotationVisitor visitEnum ]@.@" + name + descriptor + value);
            super.visitEnum(name, descriptor, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            log.debug("@.@[ModifyAnnotationVisitor visitArray:]@.@" + name);
            return super.visitArray(name);
        }

        @Override
        public void visitEnd() {
            log.debug("@.@[ModifyAnnotationVisitor visitEnd]@.@");
            super.visitEnd();
        }
    }

    static class PropertySourceMethodVisitor extends MethodVisitor {
        public PropertySourceMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            log.debug("@.@[ModifyMethodVisitor visitAnnotationDefault]@.@");
            // 访问annotation的默认值时，会执行此方法
            return new PropertySourceAnnotationVisitor(Opcodes.ASM9, super.visitAnnotationDefault());
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            log.debug("@.@[ModifyMethodVisitor visitAnnotation:" + descriptor + " visible:]@.@" + visible);
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            log.debug("@.@[ModifyMethodVisitor visitTypeAnnotation:]@.@" + descriptor);
            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public void visitEnd() {
            log.debug("@.@[ModifyMethodVisitor visitEnd]@.@");
            super.visitEnd();
        }

        @Override
        public void visitCode() {
            log.debug("@.@[ModifyMethodVisitor visitCode]@.@");
            super.visitCode();
        }
    }

    static class PropertySourceClassVisitor extends ClassVisitor {
        protected PropertySourceClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        /* 如果要处理该类上的注解，可以重写此类，在当前PropertyResource 不适用 */
        /* @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            log.debug("@.@[PropertyResourceTransformerEnhance visitAnnotation visible:{} descriptor:{}]@.@", visible, descriptor);
            // log.debug("@.@[ModifyClassVisitor descriptor:]@.@" + descriptor);
            // if ("Lorg/springframework/context/annotation/PropertySource;".equals(descriptor)) {
            //     return new ModifyAnnotationVisitor(Opcodes.ASM9);
            // }

            return super.visitAnnotation(descriptor, visible);
        } */

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            // 只修改 @PropertyResource ignoreResourceNotFound 方法
            log.info("@.@[PropertyResourceTransformerEnhance visitMethod name:{} descriptor:{}]@.@", name, descriptor);
            if ("ignoreResourceNotFound".equals(name) && "()Z".equals(descriptor)) {
                log.debug("@.@[visitMethod name:{} descriptor:{}]@.@", name, descriptor);
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new PropertySourceMethodVisitor(Opcodes.ASM9, methodVisitor);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            // log.info("@.@[access method:{}]@.@", Thread.currentThread().getStackTrace()[1].getMethodName());
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // log.info("@.@[access method:{}]@.@", Thread.currentThread().getStackTrace()[1].getMethodName());
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitEnd() {
            // log.info("@.@[access method:{}]@.@", Thread.currentThread().getStackTrace()[1].getMethodName());
            super.visitEnd();
        }
    }


}
