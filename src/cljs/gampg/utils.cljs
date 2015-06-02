(ns gampg.utils
  (:require [goog.webgl :as ggl]
            [thi.ng.geom.core :as geom]
            [thi.ng.geom.core.matrix :as mat :refer [M44]])
  (:import [goog.net XhrIo]))

;; Note this is bad form in production, it can't be optimized away
;; https://groups.google.com/d/msg/clojurescript/3QmukS-q9kw/v9dnXfWhGm8J
(def value->enum
  {0x8b89     :active-attributes
   0x84e0     :active-texture
   0x8b86     :active-uniforms
   0x846e     :aliased-line-width-range
   0x846d     :aliased-point-size-range
   0x1906     :alpha
   0x0d55     :alpha-bits
   0x0207     :always
   0x8892     :array-buffer
   0x8894     :array-buffer-binding
   0x8b85     :attached-shaders
   0x0405     :back
   0x0be2     :blend
   0x8005     :blend-color
   0x80ca     :blend-dst-alpha
   0x80c8     :blend-dst-rgb
   0x8009     :blend-equation
   0x883d     :blend-equation-alpha
   ;;0x8009     :blend-equation-rgb
   0x80cb     :blend-src-alpha
   0x80c9     :blend-src-rgb
   0x0d54     :blue-bits
   0x8b56     :bool
   0x8b57     :bool-vec2
   0x8b58     :bool-vec3
   0x8b59     :bool-vec4
   0x9244     :browser-default-webgl
   0x8764     :buffer-size
   0x8765     :buffer-usage
   0x1400     :byte
   0x0901     :ccw
   0x812f     :clamp-to-edge
   0x8ce0     :color-attachment0
   0x00004000 :color-buffer-bit
   0x0c22     :color-clear-value
   0x0c23     :color-writemask
   0x8b81     :compile-status
   0x86a3     :compressed-texture-formats
   0x8003     :constant-alpha
   0x8001     :constant-color
   0x9242     :context-lost-webgl
   0x0b44     :cull-face
   0x0b45     :cull-face-mode
   0x8b8d     :current-program
   0x8626     :current-vertex-attrib
   0x0900     :cw
   0x1e03     :decr
   0x8508     :decr-wrap
   0x8b80     :delete-status
   0x8d00     :depth-attachment
   0x0d56     :depth-bits
   0x00000100 :depth-buffer-bit
   0x0b73     :depth-clear-value
   0x1902     :depth-component
   0x81a5     :depth-component16
   0x0b74     :depth-func
   0x0b70     :depth-range
   0x84f9     :depth-stencil
   0x821a     :depth-stencil-attachment
   0x0b71     :depth-test
   0x0b72     :depth-writemask
   0x0bd0     :dither
   0x1100     :dont-care
   0x0304     :dst-alpha
   0x0306     :dst-color
   0x88e8     :dynamic-draw
   0x8893     :element-array-buffer
   0x8895     :element-array-buffer-binding
   0x0202     :equal
   0x1101     :fastest
   0x1406     :float
   0x8b5a     :float-mat2
   0x8b5b     :float-mat3
   0x8b5c     :float-mat4
   0x8b50     :float-vec2
   0x8b51     :float-vec3
   0x8b52     :float-vec4
   0x8b30     :fragment-shader
   0x8d40     :framebuffer
   0x8cd1     :framebuffer-attachment-object-name
   0x8cd0     :framebuffer-attachment-object-type
   0x8cd3     :framebuffer-attachment-texture-cube-map-face
   0x8cd2     :framebuffer-attachment-texture-level
   0x8ca6     :framebuffer-binding
   0x8cd5     :framebuffer-complete
   0x8cd6     :framebuffer-incomplete-attachment
   0x8cd9     :framebuffer-incomplete-dimensions
   0x8cd7     :framebuffer-incomplete-missing-attachment
   0x8cdd     :framebuffer-unsupported
   0x0404     :front
   0x0408     :front-and-back
   0x0b46     :front-face
   0x8006     :func-add
   0x800b     :func-reverse-subtract
   0x800a     :func-subtract
   0x8192     :generate-mipmap-hint
   0x0206     :gequal
   0x0204     :greater
   0x0d53     :green-bits
   0x8df2     :high-float
   0x8df5     :high-int
   0x1e02     :incr
   0x8507     :incr-wrap
   0x1404     :int
   0x8b53     :int-vec2
   0x8b54     :int-vec3
   0x8b55     :int-vec4
   0x0500     :invalid-enum
   0x0506     :invalid-framebuffer-operation
   0x0502     :invalid-operation
   0x0501     :invalid-value
   0x150a     :invert
   0x1e00     :keep
   0x0203     :lequal
   0x0201     :less
   0x2601     :linear
   0x2703     :linear-mipmap-linear
   0x2701     :linear-mipmap-nearest
   0x0001     :lines
   0x0002     :line-loop
   0x0003     :line-strip
   0x0b21     :line-width
   0x8b82     :link-status
   0x8df0     :low-float
   0x8df3     :low-int
   0x1909     :luminance
   0x190a     :luminance-alpha
   0x8b4d     :max-combined-texture-image-units
   0x851c     :max-cube-map-texture-size
   0x8dfd     :max-fragment-uniform-vectors
   0x84e8     :max-renderbuffer-size
   0x8872     :max-texture-image-units
   0x0d33     :max-texture-size
   0x8dfc     :max-varying-vectors
   0x8869     :max-vertex-attribs
   0x8b4c     :max-vertex-texture-image-units
   0x8dfb     :max-vertex-uniform-vectors
   0x0d3a     :max-viewport-dims
   0x8df1     :medium-float
   0x8df4     :medium-int
   0x8370     :mirrored-repeat
   0x2600     :nearest
   0x2702     :nearest-mipmap-linear
   0x2700     :nearest-mipmap-nearest
   0x0200     :never
   0x1102     :nicest
   0x0205     :notequal
   0x8004     :one-minus-constant-alpha
   0x8002     :one-minus-constant-color
   0x0305     :one-minus-dst-alpha
   0x0307     :one-minus-dst-color
   0x0303     :one-minus-src-alpha
   0x0301     :one-minus-src-color
   0x0505     :out-of-memory
   0x0d05     :pack-alignment
   0x8038     :polygon-offset-factor
   0x8037     :polygon-offset-fill
   0x2a00     :polygon-offset-units
   0x0d52     :red-bits
   0x8d41     :renderbuffer
   0x8d53     :renderbuffer-alpha-size
   0x8ca7     :renderbuffer-binding
   0x8d52     :renderbuffer-blue-size
   0x8d54     :renderbuffer-depth-size
   0x8d51     :renderbuffer-green-size
   0x8d43     :renderbuffer-height
   0x8d44     :renderbuffer-internal-format
   0x8d50     :renderbuffer-red-size
   0x8d55     :renderbuffer-stencil-size
   0x8d42     :renderbuffer-width
   0x1f01     :renderer
   0x2901     :repeat
   0x1e01     :replace
   0x1907     :rgb
   0x8d62     :rgb565
   0x8057     :rgb5-a1
   0x1908     :rgba
   0x8056     :rgba4
   0x8b5e     :sampler-2d
   0x8b60     :sampler-cube
   0x80a9     :samples
   0x809e     :sample-alpha-to-coverage
   0x80a8     :sample-buffers
   0x80a0     :sample-coverage
   0x80ab     :sample-coverage-invert
   0x80aa     :sample-coverage-value
   0x0c10     :scissor-box
   0x0c11     :scissor-test
   0x8b4f     :shader-type
   0x8b8c     :shading-language-version
   0x1402     :short
   0x0302     :src-alpha
   0x0308     :src-alpha-saturate
   0x0300     :src-color
   0x88e4     :static-draw
   0x8d20     :stencil-attachment
   0x8801     :stencil-back-fail
   0x8800     :stencil-back-func
   0x8802     :stencil-back-pass-depth-fail
   0x8803     :stencil-back-pass-depth-pass
   0x8ca3     :stencil-back-ref
   0x8ca4     :stencil-back-value-mask
   0x8ca5     :stencil-back-writemask
   0x0d57     :stencil-bits
   0x00000400 :stencil-buffer-bit
   0x0b91     :stencil-clear-value
   0x0b94     :stencil-fail
   0x0b92     :stencil-func
   0x1901     :stencil-index
   0x8d48     :stencil-index8
   0x0b95     :stencil-pass-depth-fail
   0x0b96     :stencil-pass-depth-pass
   0x0b97     :stencil-ref
   0x0b90     :stencil-test
   0x0b93     :stencil-value-mask
   0x0b98     :stencil-writemask
   0x88e0     :stream-draw
   0x0d50     :subpixel-bits
   0x1702     :texture
   0x84c0     :texture0
   0x84c1     :texture1
   0x84ca     :texture10
   0x84cb     :texture11
   0x84cc     :texture12
   0x84cd     :texture13
   0x84ce     :texture14
   0x84cf     :texture15
   0x84d0     :texture16
   0x84d1     :texture17
   0x84d2     :texture18
   0x84d3     :texture19
   0x84c2     :texture2
   0x84d4     :texture20
   0x84d5     :texture21
   0x84d6     :texture22
   0x84d7     :texture23
   0x84d8     :texture24
   0x84d9     :texture25
   0x84da     :texture26
   0x84db     :texture27
   0x84dc     :texture28
   0x84dd     :texture29
   0x84c3     :texture3
   0x84de     :texture30
   0x84df     :texture31
   0x84c4     :texture4
   0x84c5     :texture5
   0x84c6     :texture6
   0x84c7     :texture7
   0x84c8     :texture8
   0x84c9     :texture9
   0x0de1     :texture-2d
   0x8069     :texture-binding-2d
   0x8514     :texture-binding-cube-map
   0x8513     :texture-cube-map
   0x8516     :texture-cube-map-negative-x
   0x8518     :texture-cube-map-negative-y
   0x851a     :texture-cube-map-negative-z
   0x8515     :texture-cube-map-positive-x
   0x8517     :texture-cube-map-positive-y
   0x8519     :texture-cube-map-positive-z
   0x2800     :texture-mag-filter
   0x2801     :texture-min-filter
   0x2802     :texture-wrap-s
   0x2803     :texture-wrap-t
   0x0004     :triangles
   0x0006     :triangle-fan
   0x0005     :triangle-strip
   0x0cf5     :unpack-alignment
   0x9243     :unpack-colorspace-conversion-webgl
   0x9240     :unpack-flip-y-webgl
   0x9241     :unpack-premultiply-alpha-webgl
   0x1401     :unsigned-byte
   0x1405     :unsigned-int
   0x1403     :unsigned-short
   0x8033     :unsigned-short-4-4-4-4
   0x8034     :unsigned-short-5-5-5-1
   0x8363     :unsigned-short-5-6-5
   0x8b83     :validate-status
   0x1f00     :vendor
   0x1f02     :version
   0x889f     :vertex-attrib-array-buffer-binding
   0x8622     :vertex-attrib-array-enabled
   0x886a     :vertex-attrib-array-normalized
   0x8645     :vertex-attrib-array-pointer
   0x8623     :vertex-attrib-array-size
   0x8624     :vertex-attrib-array-stride
   0x8625     :vertex-attrib-array-type
   0x8b31     :vertex-shader
   0x0ba2     :viewport
   0          :zero})

(defn http-get [url cb]
  (XhrIo.send (str url)
              (fn [e]
                (let [xhr (.-target e)]
                  (cb (.getResponseText xhr))))))

(defn load-texture! [src cb]
  (let [image (js/Image.)]
    (aset image "onload"
          (cb image))
    (aset image "src" src)))

(defn load-cube-map [gl base image-extension filter wrap cb]
  (let [loader  (atom [[0 (str base "/" "posx." image-extension) false]
                       [1 (str base "/" "negx." image-extension) false]
                       [2 (str base "/" "posy." image-extension) false]
                       [3 (str base "/" "negy." image-extension) false]
                       [4 (str base "/" "posz." image-extension) false]
                       [5 (str base "/" "negz." image-extension) false]])]
    (doseq [[n src] @loader]
      (let [img (js/Image.)]
        (set! (.-onload img) ((fn [image]
                                (fn []
                                  (swap! loader (fn [loader]
                                                  (update-in loader [n] (fn [[n src _]]
                                                                          [n src img]))))
                                  (when (every? last @loader)
                                    (let [faces @loader
                                          final {:faces  {:x [(last (nth faces 0))
                                                              (last (nth faces 1))]
                                                          :y [(last (nth faces 2))
                                                              (last (nth faces 3))]
                                                          :z [(last (nth faces 4))
                                                              (last (nth faces 5))]}
                                                 :filter filter
                                                 :wrap   wrap}]
                                      (cb final))))) img))
        (set! (.-src img) src)))))
