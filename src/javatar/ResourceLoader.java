/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javatar;

import Model.DDSImageFile;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;

/**
 *
 * @author Gam
 */
public class ResourceLoader
{

    public static void extractHeroImages(Set<String> heroNames) throws IOException
    {
        String textureFile = "E:\\Programme\\Heroes of Newerth\\game\\textures.s2z";
        ZipFile zip = new ZipFile(textureFile);

        for (String hero : heroNames)
        {
            System.out.println("Extracting image for " + hero);
            ZipEntry entry = zip.getEntry("00000000/heroes/" + hero + "/icon.dds");

            try
            {
                InputStream is = zip.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(hero + ".dds");

                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer, 0, buffer.length)) > -1)
                {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            } catch (Exception e)
            {
            }
        }
    }

    public static ImageIcon renderDdsImage(String imageName, int width, int height) throws IOException
    {
        File file = new File(imageName);
        DDSImageFile ddsImageFile = new DDSImageFile(file);
        BufferedImage bufferedImage = ddsImageFile.getData();
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
        BufferedImage resizedImage = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(bufferedImage, 0, 0, width, height, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        g.dispose();

        return new ImageIcon(resizedImage);
    }

    public static Set<String> buildHeroList(String resourceFile) throws IOException
    {
        Set<String> heroes = new TreeSet<>();

        ZipFile zip = new ZipFile(resourceFile);
        Enumeration e = zip.entries();
        while (e.hasMoreElements())
        {
            ZipEntry curEntry = (ZipEntry) e.nextElement();
            String name = curEntry.getName();
            if (name.startsWith("heroes"))
            {
                heroes.add(name.split("/")[1]);
            }
        }

        heroes.remove("ability_attributeboost.entity");
        heroes.remove("ability_taunt.entity");

        return heroes;
    }
}
