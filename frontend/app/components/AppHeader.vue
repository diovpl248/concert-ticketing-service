<template>
  <header class="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-gray-100 flex items-center justify-between h-[54px] px-4">
    <div class="flex items-center gap-1 min-w-[40px]">
      <button v-if="showBack" class="w-10 h-10 -ml-2 flex items-center justify-center rounded-full hover:bg-gray-50 active:scale-95 transition-all text-gray-900" @click="goBack">
        <component :is="ChevronLeft" class="w-6 h-6 stroke-1" />
      </button>
      <slot name="left" />
    </div>

    <div class="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 text-center max-w-[60%] truncate">
      <h1 v-if="title" class="text-[17px] font-bold text-gray-900 truncate">{{ title }}</h1>
      <slot name="center" />
    </div>

    <div class="flex items-center gap-2 justify-end min-w-[40px]">
      <slot name="right" />
    </div>
  </header>
</template>

<script setup lang="ts">
import { ChevronLeft } from 'lucide-vue-next';

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  showBack: {
    type: Boolean,
    default: true
  },
  backPath: {
    type: String,
    default: ''
  }
});

const router = useRouter();

const goBack = () => {
  if (props.backPath) {
    navigateTo(props.backPath);
  } else {
    router.back();
  }
}
</script>
