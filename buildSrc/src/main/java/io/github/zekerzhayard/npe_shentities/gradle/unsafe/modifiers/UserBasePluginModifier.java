package io.github.zekerzhayard.npe_shentities.gradle.unsafe.modifiers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class UserBasePluginModifier implements IClassModifier {
    @Override
    public String getClassName() {
        return "net/minecraftforge/gradle/user/UserBasePlugin.class";
    }

    @Override
    public byte[] modify(byte[] classBytes) {
        ClassNode cn = new ClassNode();
        new ClassReader(classBytes).accept(cn, ClassReader.EXPAND_FRAMES);
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("injectIntellijRuns") && mn.desc.equals("(Lorg/w3c/dom/Document;Ljava/lang/String;)V")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.LDC) {
                        LdcInsnNode lin = (LdcInsnNode) ain;
                        if ("-Xincgc -Xmx1024M -Xms1024M".equals(lin.cst)) {
                            mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                            mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/npe_shentities/gradle/unsafe/modifiers/hooks/UserBasePluginHook", "configureClientJvmArgs", "(Lnet/minecraftforge/gradle/user/UserBasePlugin;Ljava/lang/String;)Ljava/lang/String;", false));
                        } else if ("-Xincgc -Dfml.ignoreInvalidMinecraftCertificates=true".equals(lin.cst)) {
                            mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                            mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/npe_shentities/gradle/unsafe/modifiers/hooks/UserBasePluginHook", "configureServerJvmArgs", "(Lnet/minecraftforge/gradle/user/UserBasePlugin;Ljava/lang/String;)Ljava/lang/String;", false));
                        }
                    } else if (ain.getOpcode() == Opcodes.ARRAYLENGTH) {
                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 1));
                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 3));
                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/npe_shentities/gradle/unsafe/modifiers/hooks/UserBasePluginHook", "configureRunManagerElement", "(Lorg/w3c/dom/Document;Lorg/w3c/dom/Element;)Lorg/w3c/dom/Element;", false));
                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ASTORE, 3));
                    } else if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals("org/gradle/plugins/ide/idea/model/IdeaModule") && min.name.equals("getName") && min.desc.equals("()Ljava/lang/String;")) {
                            mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/npe_shentities/gradle/unsafe/modifiers/hooks/UserBasePluginHook", "addIdeaModuleName", "(Ljava/lang/String;)Ljava/lang/String;", false));
                        }
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
