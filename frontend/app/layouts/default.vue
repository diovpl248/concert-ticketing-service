<template>
  <div class="min-h-screen bg-white pb-20">
    <slot />

    <!-- Bottom Navigation -->
    <nav class="fixed bottom-0 left-0 right-0 bg-white/95 backdrop-blur-lg border-t border-gray-100 px-6 py-3 flex justify-between items-center z-40 pb-safe">
      <NuxtLink to="/" class="flex flex-col items-center gap-1 text-black">
        <component :is="Home" class="w-6 h-6 stroke-1" />
        <span class="text-[10px] font-medium">홈</span>
      </NuxtLink>
      <NuxtLink to="/tickets" class="flex flex-col items-center gap-1 text-gray-400 hover:text-black transition-colors">
        <component :is="Ticket" class="w-6 h-6 stroke-1" />
        <span class="text-[10px] font-medium">티켓</span>
      </NuxtLink>
      <NuxtLink to="/saved" class="flex flex-col items-center gap-1 text-gray-400 hover:text-black transition-colors">
        <component :is="Heart" class="w-6 h-6 stroke-1" />
        <span class="text-[10px] font-medium">찜</span>
      </NuxtLink>
      <NuxtLink to="/profile" class="flex flex-col items-center gap-1 text-gray-400 hover:text-black transition-colors">
        <component :is="User" class="w-6 h-6 stroke-1" />
        <span class="text-[10px] font-medium">마이</span>
      </NuxtLink>
    </nav>
    <div class="h-6 w-full bg-white fixed bottom-0 left-0 right-0 z-50"></div>
  </div>
</template>

<script setup lang="ts">
import { Home, Ticket, Heart, User } from 'lucide-vue-next';
</script>

<style scoped>
.pb-safe {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
