package org.dav95s.openNTRIP.Tools.RTCM.Assets;

public enum TransformationType {
    HelmertLinearExpression(0),
    HelmertStrict(1),
    MolodenskiAbridged(2),
    MolodenskiBadekas(3);

    private int index;

    TransformationType(int i) {
        this.index = i;
    }

    public int getIndex() {
        return index;
    }
}
