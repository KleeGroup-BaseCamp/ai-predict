<template>
  <q-item>
    <div class="bg-white overview-card">
      <div class="row">
        <div class="text-h6 text-primary">Overview</div>
      </div>
      <div class="row overview-items">
        <div class="col column items-center">
          <div class="text-h6 text-primary">Bundles</div>
          <div class="text-h6 text-primary">{{ count }}</div>
        </div>
        <div class="col column items-center">
          <div class="text-h6 text-primary">Active</div>
          <div class="text-h6 text-primary">{{ active }}</div>
        </div>
      </div>
    </div>
  </q-item>
</template>

<script>
export default {
  name: "OverviewCard",
  props: {
    count: {
      type: Number,
      required: true
    },
    active: {
      type: Number,
      required: true
    }
  },
  data() {
    return {};
  }
};
</script>
