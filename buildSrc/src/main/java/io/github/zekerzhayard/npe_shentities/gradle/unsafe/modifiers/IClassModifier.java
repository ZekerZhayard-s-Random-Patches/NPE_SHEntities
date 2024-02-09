package io.github.zekerzhayard.npe_shentities.gradle.unsafe.modifiers;

public interface IClassModifier {
    String getClassName();

    byte[] modify(byte[] classBytes);
}
