export interface RuntimeEnv {
  MAPTILER_KEY: string;
  STRIPE_PUBLIC_KEY?: string;
}

declare global {
  interface Window {
    __env?: RuntimeEnv;
  }
}

export const runtimeEnv: RuntimeEnv = {
  MAPTILER_KEY: window.__env?.MAPTILER_KEY ?? '',
  STRIPE_PUBLIC_KEY: window.__env?.STRIPE_PUBLIC_KEY,
};
