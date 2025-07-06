package sdk;

public enum HeroActionType {
    MOVE,          // Di chuyển
    ATTACK,        // Tấn công cận chiến
    SHOOT,         // Bắn tầm xa (súng, cung...)
    THROW_ITEM,    // Ném vật phẩm (bom, lựu đạn...)
    USE_SPECIAL,   // Dùng kỹ năng đặc biệt
    PICKUP_ITEM,   // Nhặt vật phẩm
    USE_ITEM,      // Dùng vật phẩm đang có
    REVOKE_ITEM,   // Hủy hoặc thu hồi vật phẩm đã dùng
    DEATH,         // Trạng thái đã chết
    IDLE           // Đứng yên, không thực hiện hành động nào
}
