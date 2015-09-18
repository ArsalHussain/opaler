package it.timgreen.opal

import android.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout

import it.timgreen.opal.AnalyticsSupport._

trait SwipeRefreshSupport extends Fragment {
  import it.timgreen.opal.Bus.isSyncing
  import it.timgreen.opal.Bus.syncTrigger

  var swipeRefreshLayout: List[SwipeRefreshLayout]

  def initSwipeOptions() {
    setAppearance
    swipeRefreshLayout foreach {
      _.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        def onRefresh {
          Util.debug(s"swipe refresh")
          trackEvent("UI", "pullToRefresh", Some(this.getClass.getSimpleName))(getActivity)
          syncTrigger.fire
        }
      })
    }
  }

  override def onResume() {
    super.onResume
    isSyncing.on(tag = this) { syncing =>
      if (syncing) {
        onRefreshStart
      } else {
        onRefreshEnd
      }
    }
  }

  override def onPause() {
    isSyncing.removeByTag(this)
    super.onPause
  }

  private def setAppearance() {
    swipeRefreshLayout foreach {
      _.setColorSchemeResources(
        android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light
      )
    }
  }

  private def onRefreshStart() {
    swipeRefreshLayout foreach { srl =>
      srl.setRefreshing(true)
      srl.setEnabled(false)
    }
  }

  private def onRefreshEnd() {
    swipeRefreshLayout foreach { srl =>
      srl.setRefreshing(false)
      srl.setEnabled(true)
    }
  }
}
