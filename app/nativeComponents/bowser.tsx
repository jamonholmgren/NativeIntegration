import { requireNativeComponent } from "react-native"

/**
 * Composes `View`.
 *
 * - src: string
 * - borderRadius: number
 * - resizeMode: 'cover' | 'contain' | 'stretch'
 */
export const BowserView = requireNativeComponent("RCTBowserView")
