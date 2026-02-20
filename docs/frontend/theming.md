# TailwindCSS & Theming

We use **TailwindCSS** with a custom configuration found in `tailwind.config.ts`.

## Color Palette
We use a semantic color naming convention mapped to a cafe-inspired palette.

### Brand Colors
* `primary`: User for main actions, active states. (Warm coffee tones)
* `accent`: Used for secondary highlights. (Espresso tones)
* `neutral`: Backgrounds, text, and borders. (Slate with warmth)

### Functional Colors
* `success`: Success states, positive trends. (Fresh mint)
* `warning`: Alerts, pending states. (Warm caramel)
* `error`: Destructive actions, errors. (Berry red)

## Reusable Components
We have a set of base UI components in `app/components/ui/`:

### Button
```vue
<Button variant="primary" size="lg">Click Me</Button>
<Button variant="ghost" :loading="isLoading">Loading...</Button>
```

### Input
```vue
<Input v-model="email" label="Email" placeholder="user@example.com" />
```

### Card
```vue
<Card title="Revenue" subtitle="Today's earnings" hoverable>
  <h3>$500.00</h3>
</Card>
```

### Badge
```vue
<Badge variant="success">Completed</Badge>
```

## Dark Mode
Dark mode is supported out-of-the-box.
* Uses the `dark` class on the `<html>` tag.
* Components automatically switch colors using `dark:` variants (e.g., `bg-white dark:bg-neutral-900`).
