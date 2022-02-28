package com.zenya.nochunklag.util;

import com.zenya.nochunklag.NoChunkLag;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MetaUtils {

    public static boolean hasMeta(Player player, String meta) {
        return player.hasMetadata(meta);
    }

    public static void setMeta(Player player, String meta, Object value) {
        player.setMetadata(meta, new FixedMetadataValue(NoChunkLag.getInstance(), value));
    }

    public static void clearMeta(Player player, String meta) {
        if (hasMeta(player, meta)) {
            player.removeMetadata(meta, NoChunkLag.getInstance());
        }
    }

    public static String getMetaValue(Player player, String meta) {
        if (!(hasMeta(player, meta)) || player.getMetadata(meta).isEmpty()) {
            return "";
        }
        return player.getMetadata(meta).get(0).asString();
    }
}
