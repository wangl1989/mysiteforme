<template>
  <div class="page-wrapper">
    <div class="page-header">
      <h2 class="page-title">{{ title }}</h2>
      <slot name="header"></slot>
    </div>
    <div class="page-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
  defineProps({
    title: {
      type: String,
      default: ''
    }
  })
</script>

<style lang="scss" scoped>
  .page-wrapper {
    width: 100%;

    .page-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;

      .page-title {
        margin: 0;
        font-size: 22px;
        font-weight: 600;
        color: var(--art-text-gray-900);
      }
    }

    .page-content {
      width: 100%;
    }
  }
</style>
