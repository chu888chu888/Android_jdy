/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.utils;

import java.io.File;

import android.os.Environment;

/**
 * description :
 * 
 * @version 1.0
 * @author Owater
 * @createtime : 2015-1-13 下午5:53:59
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- Owater 2015-1-13 下午5:53:59
 * 
 */
public class URLs {

	public final static String HOST = "spreadingwind.com";
//	public final static String HOST = "192.168.100.9:8080/jdy_client";
	public final static String HTTP = "http://";

	private final static String URL_API_HOST = HTTP + HOST;

	/************************************* 微信相关URL BEGIN *************************************/

	/**
	 * 微信请求ACCESS_TOKEN URL
	 */
	public final static String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
			+ Constants.APP_ID
			+ "&secret="
			+ Constants.APPSECRET
			+ "&code=CODE&grant_type=authorization_code";
	/**
	 * 微信请求用户详细信息URL
	 */
	public final static String WX_GETUSERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/************************************* 微信相关URL END *************************************/

	public final static String TEMP_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "jdy"
			+ File.separator + "tempFile";
	/**
	 * 注册时检查手机号码唯一性 提交方式：post 参数：phone 手机号码
	 */
	public final static String CHECH_PHONE = URL_API_HOST
			+ "/member/verifyPhone";
	/**
	 * 注册时获取验证码 提交方式：post 参数：phone 手机号码
	 */
	public final static String GET_REGIRSTER_CODE_URL = URL_API_HOST
			+ "/member/verifyCode";
	/**
	 * 注册提交 提交方式 post 参数：phone 手机号码 password 密码 verifyCode 短信验证码 wcSDKOpenId
	 * 微信sdk openId unionId 微信unionId
	 */
	public final static String REGIRSTER_URL = URL_API_HOST
			+ "/member/register";
	/**
	 * 找回密码获取验证码 提交方式 post 参数：phone 手机号码
	 */
	public final static String FING_PASSWORD_GET_CORD = URL_API_HOST
			+ "/member/sendSmsChangPassword";
	/**
	 * 找回密码校验验证码 提交方式 post 参数：phone 手机号码 code 验证码
	 */
	public final static String FING_PASSWORD_CHECK_CODE = URL_API_HOST
			+ "/member/compareVerifyCode2";
	/**
	 * 找回密码的修改密码、 提交方式：post 参数：phone 手机号码 password 修改的密码
	 */
	public final static String FING_PASSWORD = URL_API_HOST
			+ "/member/updatePassword2";
	/**
	 * 获取产品列表 提交方式：get 参数：后面加数字为分页数，从0开始
	 */
	public final static String GETPRODUCTLIST = URL_API_HOST
			+ "/product/getProductList";
	/**
	 * 获取收藏列表 提交方式：post 参数：memberId 会员ID pageNumber 分页的页数
	 */
	public final static String GETFOLLOWLIST = URL_API_HOST
			+ "/product/queryMineProduct";
	/**
	 * 产品详情请求网络连接 提交方式：get 参数：产品id号+"-"+产品类别code码
	 */
	public final static String PROUDCT_DETAIL = URL_API_HOST
			+ "/product/getProductByTypeCodeAndId/";
	/**
	 * 产品webview 提交方式：get 参数：产品id号
	 */
	public final static String PROUDCT_DETAIL_WEBVIEW = URL_API_HOST
			+ "/product/getProductJsp/";
	/**
	 * 搜索按钮请求 提交方式：post 参数：keyword 用户搜索的文本 pageNumber 分页的页数
	 */
	public final static String SEARCHPRODUCT = URL_API_HOST
			+ "/product/getProdFuzzySearch";
	/**
	 * 搜索列表请求 提交方式：post 参数：prodTypeName 点击的列表项文本 pageNumber 分页的页数
	 */
	public final static String SEARCHPRODUCTLIST = URL_API_HOST
			+ "/product/getProdTypeName";
	/**
	 * 微信保存链条 提交方式：post 参数：productId 产品ID memberId 用户ID 返回数据类型：Json 参数：ID
	 * t_jdy_share_link表的ID
	 */
	public final static String WX_SHARE_URL = URL_API_HOST
			+ "/share/saveShareLink";
	/**
	 * 获取优惠码 提交方式：post 参数：productId 产品ID memberId 用户ID 返回数据类型：Json 参数：couponCode
	 * 优惠码
	 */
	public final static String WX_GETCOUPON = URL_API_HOST + "/share/getCoupon";
	/**
	 * 组装分享链条的url(无需传参，只是用来组装url)
	 */
	public final static String WX_SHARE_RESULT_URL = URL_API_HOST
			+ "/product/getProductByProid";

	/**
	 * 用户添加银行卡 提交方式：post 参数：memberId 用户的唯一id号 accountName 持卡人的姓名 bankCode 银行卡号码
	 * bankName 银行卡支行机构名称
	 */
	public final static String ADD_BANK_CARD = URL_API_HOST
			+ "/memberBank/save";
	/**
	 * 用户的所有银行卡 提交方式：get 参数：memberId 用户的唯一id号
	 */
	public final static String ALL_BANK_CARD = URL_API_HOST
			+ "/memberBank/getMemBankList";
	/**
	 * 删除银行卡 提交方式：get 参数：银行卡的id号
	 */
	public final static String DELETE_BANK_CARD = URL_API_HOST
			+ "/memberBank/delete/";
	/**
	 * 登陆 提交方式：post 参数：userName 用户账号名称 password 密码 返回json：jdycode
	 */
	public final static String LOGIN_URL = URL_API_HOST + "/member/login";// 登录
	/**
	 * 获取等级规则 提交方式 post
	 */
	public final static String GRADE = URL_API_HOST + "/member/getRankRule";
	/**
	 * 获取用户信息 提交方式：post 参数：memberId 用户的唯一id号
	 */
	public final static String USER_MESSAGE = URL_API_HOST
			+ "/member/memberList";
	/**
	 * 获取我的消息的值 提交方式：get 参数：memberId 用户的唯一id号
	 */
	public final static String USER_MY_MESSAGE = URL_API_HOST
			+ "/member/sumMessage/";
	/**
	 * 用户我的面值的数据 提交方式：post 参数：memberId 用户的唯一id号
	 */
	public final static String USER_FACE_VALUE = URL_API_HOST
			+ "/member/incomeExpendList";
	/**
	 * 产品经理信息 提交方式：get 参数：产品ID
	 */
	public final static String MANAGER_INFO = URL_API_HOST
			+ "/product/getCustManagerByprodId/";
	/**
	 * 获取会员银行卡信息
	 */
	public final static String BANKCARD_URL = URL_API_HOST
			+ "/memberBank/getMemBank";

	/**
	 * 获取我的订单的数据 提交方式：post 参数：memberId 用户的唯一id号
	 */
	public final static String MY_ORDER = URL_API_HOST + "/member/orderList";
	/**
	 * 提现记录 提交方式：post 参数：memberId 用户的唯一id号
	 */
	public final static String MY_RECORD = URL_API_HOST
			+ "/member/withcashrecordList";
	/**
	 * 提取现金操作
	 */
	public final static String GETCASH_URL = URL_API_HOST
			+ "/withdrawcash/save";

	/**
	 * 获取可用提现金额
	 */
	public final static String CanWithdCash_URL = URL_API_HOST
			+ "/withdrawcash/getCanWithdCash";
	/**
	 * 已读消息
	 */
	public final static String READEDMSG_URL = URL_API_HOST
			+ "/member/updateMessageRead";
	/**
	 * 未读消息推送
	 */
	public final static String HASMSG_URL = URL_API_HOST
			+ "/member/hasMessageUnRead";
	/**
	 * 修改昵称 提交方式：post 参数 memberId 用户唯一id号 name 修改的昵称
	 */
	public final static String UPDATE_USERNAME = URL_API_HOST
			+ "/member/updateName";
	/**
	 * 修改密码 提交方式：post 参数：memberId 用户的唯一id号 oldPassword 用户的旧密码 newPassword 用户的新密码
	 */
	public final static String CHANGE_PASSWORD = URL_API_HOST
			+ "/member/updatePassword";

	/**
	 * 消息列表
	 */
	public final static String MSGLIST_URL = URL_API_HOST
			+ "/member/messageList";

	/**
	 * 反馈订单提交 提交方式:post 参数: 本方法需要文件提交,android下是用HttpURLConnection模拟post提交
	 * 除文件内容,额外参数还有: memberId:用户的唯一id号 businessType:固定写成"OrderfbEntity"
	 * coupon:填写的优惠码(限制字母数字,不能是符号) buyMoney:购买金额(精确到2位小数点)
	 * buyTime:购买日期("yyyy-MM-dd"格式)
	 * 
	 */
	public final static String FEEDBACK_URL = URL_API_HOST + "/fileUpload";

	/**
	 * 用户头像更改 提交方式:post 参数: 本方法需要文件提交,android下是用HttpURLConnection模拟post提交
	 * 除文件内容,额外参数还有: memberId:用户的唯一id号
	 */
	public final static String PORTRAIT_UPLOAD = URL_API_HOST + "/fileUpload";

	/**
	 * 我的面子 提交方式：get 参数："memberId="+用户的唯一id号+ "&year="年份
	 */
	public final static String MY_FACE = URL_API_HOST + "/member/face?";
	/**
	 * 我的圈子 提交方式：get 参数："memberId="+用户的唯一id号+ "&year="年份
	 */
	public final static String MY_COMMUNITY = URL_API_HOST
			+ "/member/community?";
	/**
	 * 退出登录
	 */
	public final static String LOGINOU_URL = URL_API_HOST + "/member/loginOut";
	/**
	 * 图片轮播下载地址 提交方式：get 参数：(此URL后加0为请求第一张图片，加1为请求第二张图片，加2为请求第三张图片)
	 */
	public final static String DOWNBITMAP_URL = URL_API_HOST
			+ "/carousel/findImage/";
	/**
	 * 收藏按钮搜索 提交方式：post 参数：memberId 会员ID pageNumber 分页的页数 keyword 用户搜索的文本
	 */
	public final static String FOLLOWBTN_URL = URL_API_HOST
			+ "/product/queryMineFuzzySearch";
	/**
	 * 收藏列表搜索参数 提交方式：post 参数：memberId 会员ID pageNumber 分页的页数 prodTypeName
	 * 点击的列表项文本
	 */
	public final static String FOLLOWLIST_URL = URL_API_HOST
			+ "/product/queryMineProduct";
	/**
	 * 换银行卡 提交方式：post 参数：ID 银行卡的id号
	 */
	public final static String CHANGE_HAND_CARD = URL_API_HOST
			+ "/memberBank/updateTime";

	/**
	 * 更新分享次数 提交方式：post 参数：productId 产品ID memberId 用户ID
	 */
	public final static String SHARE_COUNT_URL = URL_API_HOST
			+ "/share/updateShareCount";
	/**
	 * 获得最新版本 提交方式：get
	 */
	public final static String GETNEWAPKVERSION = URL_API_HOST
			+ "/appsetting/getNewVersion";
	/**
	 * 下载新版本apk 提交方式：get
	 */
	public static String GETNEWAPK = URL_API_HOST + "/appsetting/getNewApk";
	/**
	 * 获取星品的数据 提交方式 post
	 */
	public static String SUPERPRODUCT = URL_API_HOST
			+ "/product/getSuperProduct";
	/**
	 * 获取关于我们(公司介绍)
	 */
	public static String COMPANY_DESCRIBE = URL_API_HOST
			+ "/more/conpanyIntroduce";
	/**
	 * 上线介绍
	 */
	public static String SHANGXIAN_INTRODUCE = URL_API_HOST
			+ "/more/shangxianIntroduce";
	/**
	 * 红包介绍
	 */
	public static String HONGBAO_INTRODUCE = URL_API_HOST
			+ "/more/hongbaoIntroduce";
}
