package io.github.zekerzhayard.npe_shentities.gradle.unsafe.modifiers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AssetsThreadModifier implements IClassModifier {
    @Override
    public String getClassName() {
        return "net/minecraftforge/gradle/tasks/DownloadAssetsTask$AssetsThread.class";
    }

    @Override
    public byte[] modify(byte[] classBytes) {
        ClassNode cn = new ClassNode();
        new ClassReader(classBytes).accept(cn, ClassReader.EXPAND_FRAMES);
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("run") && mn.desc.equals("()V")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.LDC) {
                        LdcInsnNode lin = (LdcInsnNode) ain;
                        if ("http://resources.download.minecraft.net/".equals(lin.cst)) {
                            lin.cst = "https://resources.download.minecraft.net/";
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
