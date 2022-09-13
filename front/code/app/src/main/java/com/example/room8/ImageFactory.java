package com.example.room8;

/**
 * Factory fot images resources
 */
public final class ImageFactory {

    public static final int[] ungenderAndAnimalsImgs = new int[]{};
    public static final int[] manImgs = new int[]{};
    public static final int[] womanImgs = new int[]{};

    public static int profileImageFactory(int iconId) {
//       if(iconId < 0)
//        return defaultImg;

        if (iconId < ungenderAndAnimalsImgs.length)
            return ungenderAndAnimalsImgs[iconId];

        iconId = iconId - ungenderAndAnimalsImgs.length;

        if (iconId < manImgs.length)
            return manImgs[iconId];

        iconId = iconId - manImgs.length;

        if (iconId < womanImgs.length)
            return womanImgs[iconId];

        return 0;

    }

    public static int taskImageFactory(String taskName) {
        switch (taskName) {
            case "cleaning":
                return R.drawable.icon_cleaning;
            case "dish":
                return R.drawable.icon_dishwasher;
            case "laundry":
                return R.drawable.icon_laundry_machine;
            case "hang laundry":
                return R.drawable.icon_socks;
            case "iron":
                return R.drawable.icon_iron;
            case "leash":
                return R.drawable.icon_leash;
            default:
                return R.drawable.icon_default_task;
        }
    }
}