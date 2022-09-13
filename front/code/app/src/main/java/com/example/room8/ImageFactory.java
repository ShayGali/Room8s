package com.example.room8;
/**
Factory fot images resources
 */
public final class ImageFactory{

    public static final int[] ungenderAndAnimalsImgs = new int[]{};
    public static final int[] manImgs = new int[]{};
    public static final int[] womanImgs = new int[]{};

   public static int profileImageFactory(int iconId){
       if(iconId < 0)
        return defaultImg;
    
        if(iconId < ungenderAndAnimalsImgs.length)
            return ungenderAndAnimalsImgs[iconId];

        iconId = iconId - ungenderAndAnimalsImgs.length;

        if(iconId < manImgs.length)
            return manImgs[iconId];

        iconId = iconId - manImgs.length;

        if(iconId < womanImgs.length)
            return womanImgs[iconId];

        return defaultImg;

   }
//
//    public static int taskImageFactory(int iconId){
//        switch (iconId){
//            case 1:
//                return ?;
//            default:
//                return ?;
//        }
//    }
}