// This file is based on code from Sodium by JellySquid, licensed under the LGPLv3 license.

package net.coderbot.iris.gl.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import net.coderbot.iris.gl.IrisRenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL20C;

public class ProgramCreator {
	private static final Logger LOGGER = LogManager.getLogger(ProgramCreator.class);

	public static int create(String name, GlShader... shaders) {
		int program = GlStateManager.glCreateProgram();

		// TODO: This is *really* hardcoded, we need to refactor this to support external calls
		// to glBindAttribLocation
		IrisRenderSystem.bindAttributeLocation(program, 10, "mc_Entity");
		IrisRenderSystem.bindAttributeLocation(program, 11, "mc_midTexCoord");
		IrisRenderSystem.bindAttributeLocation(program, 12, "at_tangent");

		for (GlShader shader : shaders) {
			GlStateManager.glAttachShader(program, shader.getHandle());
		}

		GlStateManager.glLinkProgram(program);

        //Always detach shaders according to https://www.khronos.org/opengl/wiki/Shader_Compilation#Cleanup
        for (GlShader shader : shaders) {
            IrisRenderSystem.detachShader(program, shader.getHandle());
        }

		String log = IrisRenderSystem.getProgramInfoLog(program);

		if (!log.isEmpty()) {
			LOGGER.warn("Program link log for " + name + ": " + log);
		}

		int result = GlStateManager.glGetProgrami(program, GL20C.GL_LINK_STATUS);

		if (result != GL20C.GL_TRUE) {
			throw new RuntimeException("Shader program linking failed, see log for details");
		}

		return program;
	}
}
