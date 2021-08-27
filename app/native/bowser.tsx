import React from "react"
import { requireNativeComponent, ViewProps } from "react-native"

type BowserEvent = {
  message: string
}
interface BowserViewProps extends ViewProps {
  onPress: (event: BowserEvent) => void
}

export const BowserView = (props: BowserViewProps) => {
  return <RCTBowserView {...props} onPress={(event) => props.onPress(event.nativeEvent)} />
}

const RCTBowserView = requireNativeComponent("RCTBowserView")
