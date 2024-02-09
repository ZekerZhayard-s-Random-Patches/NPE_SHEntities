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

public class BasePluginModifier implements IClassModifier {
    @Override
    public String getClassName() {
        return "net/minecraftforge/gradle/common/BasePlugin.class";
    }

    @Override
    public byte[] modify(byte[] classBytes) {
        ClassNode cn = new ClassNode();
        new ClassReader(classBytes).accept(cn, ClassReader.EXPAND_FRAMES);
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("makeObtainTasks") && mn.desc.equals("()V")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.LDC) {
                        LdcInsnNode lin = (LdcInsnNode) ain;
                        if ("http://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/{MC_VERSION}.jar".equals(lin.cst)) {
                            lin.cst = "{CLIENT_JAR_URL}";
                        } else if ("http://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/minecraft_server.{MC_VERSION}.jar".equals(lin.cst)) {
                            lin.cst = "{SERVER_JAR_URL}";
                        } else if ("https://s3.amazonaws.com/Minecraft.Download/indexes/{ASSET_INDEX}.json".equals(lin.cst)) {
                            lin.cst = "{ASSETS_INDEX_URL}";
                        } else if ("http://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/{MC_VERSION}.json".equals(lin.cst)) {
                            lin.cst = "{VERSION_JSON_URL}";
                        }
                    }
                }
            } else if (mn.name.equals("resolve") && mn.desc.equals("(Ljava/lang/String;Lorg/gradle/api/Project;Lnet/minecraftforge/gradle/common/BaseExtension;)Ljava/lang/String;")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.ARETURN) {
                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/npe_shentities/gradle/unsafe/modifiers/hooks/BasePluginHook", "resolve", "(Ljava/lang/String;Lnet/minecraftforge/gradle/common/BasePlugin;)Ljava/lang/String;", false));
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
