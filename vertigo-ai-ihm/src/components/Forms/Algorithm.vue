<template>
  <q-card class="create-card">
    <q-card-section>
      <div class="text-subtitle1 text-primary">Algorithm</div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <div class="row justify-center">
        <div class="column col-6 q-pa-sm">
          <q-input
            filled
            class="col-5"
            label="Package"
            v-model="algorithm.package"
          />
        </div>
        <div class="column col-6 q-pa-sm">
          <q-input filled class="col-5" label="Name" v-model="algorithm.name" />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Algorithm",
  data() {
    return {
      algorithm: {
        package: "",
        name: ""
      }
    };
  }
};
</script>
