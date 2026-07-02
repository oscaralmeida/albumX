package com.albumx.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "album")
public class AlbumProperties {

    private int stickerCount = 700;
    private TradeProperties trade = new TradeProperties();

    public int getStickerCount() {
        return stickerCount;
    }

    public void setStickerCount(int stickerCount) {
        this.stickerCount = stickerCount;
    }

    public TradeProperties getTrade() {
        return trade;
    }

    public void setTrade(TradeProperties trade) {
        this.trade = trade;
    }

    public static class TradeProperties {

        private boolean protectSingleSticker = false;

        public boolean isProtectSingleSticker() {
            return protectSingleSticker;
        }

        public void setProtectSingleSticker(boolean protectSingleSticker) {
            this.protectSingleSticker = protectSingleSticker;
        }
    }
}
