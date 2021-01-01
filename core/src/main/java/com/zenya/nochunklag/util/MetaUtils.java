package com.zenya.nochunklag.util;

import com.zenya.nochunklag.NoChunkLag;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MetaUtils {
    private static MetaUtils metaUtils;

    public boolean hasMeta(Player player, String meta) {
        return player.hasMetadata(meta);
    }

    public void setMeta(Player player, String meta, Object value) {
        player.setMetadata(meta, new FixedMetadataValue(NoChunkLag.getInstance(), value));
    }

    public void clearMeta(Player player, String meta) {
        if(hasMeta(player, meta)) {
            player.removeMetadata(meta, NoChunkLag.getInstance());
        }
    }

    public String getMetaValue(Player player, String meta) {
        if(!(hasMeta(player, meta)) || player.getMetadata(meta).size() == 0) return "";
        return player.getMetadata(meta).get(0).asString();
    }

    public static MetaUtils getInstance() {
        if(metaUtils == null) {
            metaUtils = new MetaUtils();
        }
        return metaUtils;
    }
}

