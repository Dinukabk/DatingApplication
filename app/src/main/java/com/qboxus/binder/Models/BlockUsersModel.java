package com.qboxus.binder.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockUsersModel{
 @JsonProperty("BlockUser")
 public BlockUserModel blockUserModel;
 @JsonProperty("BlockedUser")
 public BlockedUserModel blockedUserModel;
}
