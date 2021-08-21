<template>
  <q-card class="column">
    <q-card-section class="row">
      <div class="text-h6 text-primary self-start">{{ bundle.name }}</div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section class="row justify-between">
      <div class="column col-12">
        <div class="text-subtitle1 text-primary ">Description</div>
        <div class="text-body2 text-dark">
          {{ bundle.description }}
        </div>
      </div>
      <div class="column col-10">
        <div class="text-subtitle1 text-primary ">Datasource</div>
        <div class="text-body2 text-accent">
          <a
            v-if="bundle.datasource.startsWith('http')"
            href="bundle.datasource"
            >{{ bundle.datasource }}</a
          >
          <div v-else>{{ bundle.datasource }}</div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "BundleSummary",
  data() {
    return {};
  },
  props: {
    bundle: Object
  }
};
</script>
