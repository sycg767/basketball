import { defineStore } from 'pinia';
import { login, getUserInfo as fetchUserInfo } from '@/api/user';
import { setToken, removeToken, setUserInfo, removeUserInfo, getToken, getUserInfo as getStoredUserInfo } from '@/utils/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getStoredUserInfo() || {},
    roles: []
  }),

  getters: {
    userId: (state) => state.userInfo.id,
    username: (state) => state.userInfo.username,
    realName: (state) => state.userInfo.realName,
    memberLevel: (state) => state.userInfo.memberLevel || 0,
    isLogin: (state) => !!state.token
  },

  actions: {
    /**
     * 用户登录
     */
    async login(loginForm) {
      try {
        const res = await login(loginForm);
        this.token = res.data.token;
        this.userInfo = res.data.userInfo;
        setToken(res.data.token);
        setUserInfo(res.data.userInfo);
        return Promise.resolve(res);
      } catch (error) {
        return Promise.reject(error);
      }
    },

    /**
     * 设置Token
     */
    setToken(token) {
      this.token = token;
      setToken(token);
    },

    /**
     * 设置用户信息
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      setUserInfo(userInfo);
    },

    /**
     * 获取用户信息
     */
    async getUserInfo() {
      try {
        const res = await fetchUserInfo();
        this.userInfo = res.data;
        setUserInfo(res.data);
        return Promise.resolve(res);
      } catch (error) {
        return Promise.reject(error);
      }
    },

    /**
     * 登出
     */
    logout() {
      this.token = '';
      this.userInfo = {};
      this.roles = [];
      removeToken();
      removeUserInfo();
    }
  }
});