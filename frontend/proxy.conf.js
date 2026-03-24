module.exports = {
  "/api": {
    "target": "http://127.0.0.1:8080",
    "secure": false,
    "changeOrigin": false,
    "logLevel": "debug"
  },
  "/ai-api": {
    "target": "http://127.0.0.1:8081",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {
      "^/ai-api": ""
    },
    "onProxyReq": function(proxyReq, req, res) {
      if (req.headers.authorization) {
        proxyReq.setHeader('Authorization', req.headers.authorization);
      }
    },
    "logLevel": "debug"
  }
};
