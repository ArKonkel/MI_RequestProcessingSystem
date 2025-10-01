import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from "@tailwindcss/vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    //port: 5174,
    proxy: {
      '/api': 'http://localhost:8080',
      '/stompbroker': {
        target: 'http://localhost:8080/',
        ws: true,
        changeOrigin: true,
        configure(proxy) {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Proxying request to:', proxyReq.path);
          });
          proxy.on('error', (err) => {
            console.error('Proxy error:', err);
          });
        }
      }
    }
  }
})
