@Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();

    // ✅ Always allow preflight
    if (request.getMethod().equals("OPTIONS")) {
        filterChain.doFilter(request, response);
        return;
    }

    // ✅ Optional: skip JWT for public endpoints (extra safety layer)
    if (path.contains("/auth") || path.contains("/requests/estimate-fare")) {
        filterChain.doFilter(request, response);
        return;
    }

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);

    String email;

    try {
        email = jwtUtil.extractEmail(token);
    } catch (Exception e) {
        filterChain.doFilter(request, response);
        return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        if (jwtUtil.validateToken(token)) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    filterChain.doFilter(request, response);
}
