import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        rewrite: (path) => path // 保持原路径
      },
      '/files': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        rewrite: (path) => path // 保持原路径
      },
      '/uploads': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        rewrite: (path) => path // 保持原路径
      }
    }
  }
}); 