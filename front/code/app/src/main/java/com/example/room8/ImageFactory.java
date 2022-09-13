package com.example.room8;

/**
 * Factory fot images resources
 */
public final class ImageFactory {

    public static final int[] ungenderAndAnimalsImgs = new int[]{
            R.drawable.profile_banner, R.drawable.ungender, R.drawable.ungender_1, R.drawable.ungender_2, R.drawable.ungender_3,
            R.drawable.icon_bear, R.drawable.icon_bee, R.drawable.icon_cat, R.drawable.icon_zebra,
            R.drawable.icon_chicken, R.drawable.icon_dog, R.drawable.icon_dog_1, R.drawable.icon_dog_2,
            R.drawable.icon_dragon, R.drawable.icon_duck, R.drawable.icon_elephant, R.drawable.icon_goat,
            R.drawable.icon_gorilla, R.drawable.icon_hen, R.drawable.icon_monkey, R.drawable.icon_monkey_2,
            R.drawable.icon_octopus, R.drawable.icon_penguin, R.drawable.icon_sloth, R.drawable.icon_snake_0,
            R.drawable.icon_snake_1, R.drawable.icon_tiger, R.drawable.icon_turtle, R.drawable.icon_wild_boar
    };
    public static final int[] manImgs = new int[]{
            R.drawable.icon_man_1, R.drawable.icon_man_2, R.drawable.icon_man_3, R.drawable.icon_man_4,
            R.drawable.icon_man_5, R.drawable.icon_man_6, R.drawable.icon_man_7, R.drawable.icon_man_8,
            R.drawable.icon_man_9, R.drawable.icon_man_10, R.drawable.icon_man_11, R.drawable.icon_man_12,
            R.drawable.icon_man_13, R.drawable.icon_man_14, R.drawable.icon_man_15, R.drawable.icon_man_16,
            R.drawable.icon_man_17


    };
    public static final int[] womanImgs = new int[]{
            R.drawable.icon_woman_1, R.drawable.icon_woman_2, R.drawable.icon_woman_3, R.drawable.icon_woman_4,
            R.drawable.icon_woman_5, R.drawable.icon_woman_6, R.drawable.icon_woman_7, R.drawable.icon_woman_8,
            R.drawable.icon_woman_9, R.drawable.icon_woman_10, R.drawable.icon_woman_11, R.drawable.icon_woman_12,
            R.drawable.icon_woman_13, R.drawable.icon_woman_14, R.drawable.icon_woman_15, R.drawable.icon_woman_16,
            R.drawable.icon_woman_17, R.drawable.icon_woman_18

    };

    public static int profileImageFactory(int iconId) {
        if (iconId < 0)
            return ungenderAndAnimalsImgs[0];

        if (iconId < ungenderAndAnimalsImgs.length)
            return ungenderAndAnimalsImgs[iconId];

        iconId = iconId - ungenderAndAnimalsImgs.length;

        if (iconId < manImgs.length)
            return manImgs[iconId];

        iconId = iconId - manImgs.length;

        if (iconId < womanImgs.length)
            return womanImgs[iconId];

        return ungenderAndAnimalsImgs[0];

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