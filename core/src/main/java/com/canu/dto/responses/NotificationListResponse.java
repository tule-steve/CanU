package com.canu.dto.responses;

import com.canu.model.NotificationModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotificationListResponse {

    List<NotificationModel> detail;

    Long unreadCount;
}
