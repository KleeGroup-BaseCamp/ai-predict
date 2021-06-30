<template>
  <q-card
    clickable
    class="q-pa-sm version-card"
    @click="select()"
    :style="color()"
  >
    <div
      v-if="isSelected()"
      class=" row col-md-3 text-white text-subtitle1 offset-md-1 items-center"
    >
      <div class="col self-start">Version {{ version }}</div>
      <q-badge class="text-white"> score: {{ score }} </q-badge>
      <q-icon v-if="isActive()" class="text-white" name="check_circle" />
      <q-icon v-if="isBest()" class="text-warning" name="star" />
    </div>
    <div
      v-else
      class=" row col-md-3 text-primary text-subtitle1 offset-md-1 items-center"
    >
      <div class="col self-start">Version {{ version }}</div>
      <q-badge color="white" class="text-primary"> score: {{ score }} </q-badge>
      <q-icon v-if="isActive()" name="check_circle" />
      <q-icon v-if="isBest()" class="text-warning" name="star" />
    </div>
  </q-card>
</template>

<script>
export default {
  name: "VersionCard",
  data() {
    return {};
  },
  props: {
    version: {
      type: Number,
      required: true
    },
    selectedVersion: {
      type: Number,
      required: true
    },
    activeVersion: {
      required: true
    },
    bestVersion: {
      type: Number,
      required: true
    },
    score: {
      type: Number,
      required: true
    }
  },
  methods: {
    select() {
      if (!this.isSelected()) {
        this.$emit("selected", this.version);
      }
    },
    isSelected() {
      return this.version == this.selectedVersion;
    },
    isActive() {
      return this.version == this.activeVersion;
    },
    isBest() {
      return this.version == this.bestVersion;
    },
    color() {
      if (this.isSelected()) {
        return "background-color: #264653; ";
      } else {
        return "background-color: white;";
      }
    }
  }
};
</script>
